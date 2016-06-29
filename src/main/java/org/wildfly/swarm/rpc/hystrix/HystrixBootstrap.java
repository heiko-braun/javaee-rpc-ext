package org.wildfly.swarm.rpc.hystrix;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
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

    final MetricRegistry metrics = new MetricRegistry();

    @PostConstruct
    public void onStartup() {
        System.out.println("Initialising hystrix ...");
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new EEConcurrencyStrategy(threadFactory));
        HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metrics));
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("Shutting down hystrix ...");
        Hystrix.reset(1, TimeUnit.SECONDS);

        ConsoleReporter.forRegistry(metrics).outputTo(System.out).build().report();
    }

}