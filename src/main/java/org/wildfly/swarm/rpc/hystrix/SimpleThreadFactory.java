package org.wildfly.swarm.rpc.hystrix;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
@ApplicationScoped
class SimpleThreadFactory implements ManagedThreadFactory {

    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

}
