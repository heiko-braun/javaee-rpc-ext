package org.wildfly.swarm.rpc.discovery;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
public interface ServiceDiscovery {

    ServiceTargets getServiceAddresses(String serviceName);
}
