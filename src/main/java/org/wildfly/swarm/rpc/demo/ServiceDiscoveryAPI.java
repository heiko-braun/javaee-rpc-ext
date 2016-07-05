package org.wildfly.swarm.rpc.demo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.wildfly.swarm.rpc.api.Service;
import org.wildfly.swarm.rpc.api.ServiceDiscovery;
import org.wildfly.swarm.rpc.api.ServiceTargets;

/**
 * @author Heiko Braun
 * @since 04/07/16
 */
@ApplicationScoped
public class ServiceDiscoveryAPI {

    /**
     * A client that manually iterates over the list of discovered services
     */
    @Inject
    @ServiceDiscovery(service = "date-service")
    private ServiceTargets<Service> serviceTargets;

    /**
     * Services come and go
     */
    public void activeServices() {
        List<Service> activeServices = serviceTargets.current().stream()
                .filter(Service::isAlive)
                .collect(Collectors.toList());
        System.out.println("Active services: "+ activeServices.size());
    }

    /**
     * Derive web targets for use in JAX-RS client API's
     */
    public String serviceDiscovery() {
        Client client = ClientBuilder.newClient();

        Optional<WebTarget> target = serviceTargets.current().stream()
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
