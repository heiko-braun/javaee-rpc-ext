package org.wildfly.swarm.rpc.hystrix;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.enterprise.inject.spi.BeanManager;
import javax.interceptor.InvocationContext;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.jboss.weld.bean.ManagedBean;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
public class GenericCommand extends HystrixCommand<Object> {

    private final BeanManager beanManager;

    private InvocationContext ic;

    private final Optional<String> fallback;

    public GenericCommand(BeanManager beanManager, InvocationContext ic, Optional<String> fallback) {

        super(
                Setter.withGroupKey(
                        HystrixCommandGroupKey.Factory.asKey(ic.getMethod().getDeclaringClass().getSimpleName()))
                                .andCommandKey(
                                        HystrixCommandKey.Factory.asKey(ic.getMethod().getName())
                                )

        );

        this.beanManager = beanManager;
        this.ic = ic;
        this.fallback = fallback;
    }

    @Override
    protected Object run() throws Exception {
        return ic.proceed();
    }

    @Override
    protected Object getFallback() {

        Object result = null;

        if(fallback.isPresent()) {
            try {
                Class<?> declaringClass = ic.getMethod().getDeclaringClass();
                ManagedBean delegate = (ManagedBean) beanManager.getBeans(declaringClass).iterator().next();

                Method method = declaringClass.getMethod(fallback.get());
                result = method.invoke(delegate.create(beanManager.createCreationalContext(null)));
            } catch (Throwable t) {
                throw new RuntimeException("Failed to resolve fallback", t);
            }
        }

        return result;
    }
}
