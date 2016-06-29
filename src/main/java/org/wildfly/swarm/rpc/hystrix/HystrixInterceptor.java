package org.wildfly.swarm.rpc.hystrix;

import java.io.Serializable;
import java.util.Optional;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.wildfly.swarm.rpc.api.IsolatedCommand;

@IsolatedCommand
@Interceptor
public class HystrixInterceptor implements Serializable {


    @Inject
    BeanManager beanManager;

    public HystrixInterceptor() {
    }

    @AroundInvoke
    public Object wrapCommand(InvocationContext invocationContext)
            throws Exception {

        IsolatedCommand metaData = invocationContext.getMethod().getAnnotation(IsolatedCommand.class);
        Optional<String> fallback = metaData.fallbackMethod()!=null ? Optional.of(metaData.fallbackMethod()) : Optional.empty();

        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            System.out.println("Wrapping method: "
                    + invocationContext.getMethod().getName() + " in class "
                    + invocationContext.getMethod().getDeclaringClass().getName());

            return new GenericCommand(beanManager, invocationContext, fallback).execute();

        } finally {
            context.close();
        }
    }
}