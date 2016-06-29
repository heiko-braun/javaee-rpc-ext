package org.wildfly.swarm.rpc.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.wildfly.swarm.rpc.hystrix.HystrixBootstrap;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
@ApplicationScoped
public class DemoBean {

    @Inject
    HystrixBootstrap hystrixEnv;

    @Inject
    ClientAPIDemo clientAPI;

    public void begin() {

        String date = clientAPI.hystrixEncupsulatedInvocation();
        System.out.println("Received " +date);
    }
}
