package org.wildfly.swarm.rpc.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.wildfly.swarm.rpc.hystrix.HystrixBootstrap;
import rx.Observable;
import rx.Observer;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
@ApplicationScoped
public class DemoDriverBean {

    @Inject
    HystrixBootstrap hystrixEnv;

    @Inject
    FaultIsolationAPI faultIsolationAPI;

    @Inject
    ServiceDiscoveryAPI serviceDisocveryAPI;

    public void begin() {

        // simple isolated command
        String date = faultIsolationAPI.syncIsolatedCommand();
        System.out.println("Received sync" +date);

        // async isolated command
        Observable observable = faultIsolationAPI.asyncIsolatedCommand();
        observable.subscribe(new Observer() {

            @Override
            public void onCompleted() {
                System.out.println("Received async completed");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object v) {
                System.out.println("Received async: " + v);
            }

        });

        // manual service discovery
        date = serviceDisocveryAPI.serviceDiscovery();
        System.out.println("Received sync" +date);

        // services come and go
        serviceDisocveryAPI.activeServices();

    }
}
