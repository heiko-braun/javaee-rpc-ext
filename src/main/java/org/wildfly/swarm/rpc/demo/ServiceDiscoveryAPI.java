package org.wildfly.swarm.rpc.demo;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.wildfly.swarm.rpc.api.ServiceDiscovery;
import org.wildfly.swarm.rpc.discovery.Service;
import org.wildfly.swarm.rpc.discovery.ServiceTargets;

/**
 * @author Heiko Braun
 * @since 04/07/16
 */
@ApplicationScoped
public class ServiceDiscoveryAPI {

    /**
     * A cient that manually iterates over the list of discovered services
     */
    @Inject
    @ServiceDiscovery(service = "date-service")
    private ServiceTargets<Service> serviceTargets;

    public String serviceDiscovery() {
        Client client = ClientBuilder.newClient();

        Optional<WebTarget> target = serviceTargets.get().stream()
                .filter(Service::isAlive)
                .findFirst()
                .map(s -> client.target(s.asHttp()));

        if(target.isPresent()) {
            Response response = target.get().request(MediaType.APPLICATION_JSON).get();
            Assert.assertEquals(200, response.getStatus());
            return response.readEntity(String.class);
        } else {
            throw new RuntimeException("No active servers found");
        }
    }

}
