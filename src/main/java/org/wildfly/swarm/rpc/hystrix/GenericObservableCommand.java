package org.wildfly.swarm.rpc.hystrix;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.enterprise.inject.spi.BeanManager;
import javax.interceptor.InvocationContext;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.jboss.weld.bean.ManagedBean;
import rx.Observable;


/**
 * @author Heiko Braun
 * @since 29/06/16
 */
public class GenericObservableCommand extends HystrixObservableCommand<Object> {

    private final BeanManager beanManager;

    private InvocationContext ic;

    private final Optional<String> fallback;

    public GenericObservableCommand(BeanManager beanManager, InvocationContext ic, Optional<String> fallback) {

        super(
                HystrixObservableCommand.Setter
                        .withGroupKey(
                            HystrixCommandGroupKey.Factory.asKey(ic.getMethod().getDeclaringClass().getSimpleName())
                        )
                        .andCommandKey(
                                HystrixCommandKey.Factory.asKey(ic.getMethod().getName())
                        )

        );

        this.beanManager = beanManager;
        this.ic = ic;
        this.fallback = fallback;
    }

    @Override
    protected Observable<Object> construct() {
        try {
            return ((Observable)ic.proceed());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    protected Observable<Object> resumeWithFallback() {
        Observable result = Observable.empty();

        if(fallback.isPresent()) {
            try {
                Class<?> declaringClass = ic.getMethod().getDeclaringClass();
                ManagedBean delegate = (ManagedBean) beanManager.getBeans(declaringClass).iterator().next();

                Method method = declaringClass.getMethod(fallback.get());
                result = Observable.just(
                        method.invoke(
                                delegate.create(beanManager.createCreationalContext(null))
                        )
                );
            } catch (Throwable t) {
                throw new RuntimeException("Failed to resolve fallback", t);
            }
        }

        return result;
    }

}
