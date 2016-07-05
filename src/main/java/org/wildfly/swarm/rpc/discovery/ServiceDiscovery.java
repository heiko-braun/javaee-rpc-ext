package org.wildfly.swarm.rpc.discovery;

import org.wildfly.swarm.rpc.api.Service;
import org.wildfly.swarm.rpc.api.ServiceTargets;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
public interface ServiceDiscovery {

    ServiceTargets<Service> getServiceTargets(String serviceName);
}
