package org.wildfly.swarm.rpc.hystrix;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;

/**
 * @author Heiko Braun
 * @since 01/06/16
 */
@Singleton
@Startup
public class HystrixBootstrap {

    @Inject
    ManagedThreadFactory threadFactory;

    @PostConstruct
    public void onStartup() {
        System.out.println("Initialising hystrix ...");
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new EEConcurrencyStrategy(threadFactory));
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("Shutting down hystrix ...");
        Hystrix.reset(1, TimeUnit.SECONDS);
    }
}