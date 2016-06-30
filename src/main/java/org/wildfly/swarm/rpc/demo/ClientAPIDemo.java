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
import org.wildfly.swarm.rpc.api.CircuitBreaker;
import org.wildfly.swarm.rpc.api.ServerList;
import org.wildfly.swarm.rpc.discovery.Server;
import org.wildfly.swarm.rpc.discovery.ServiceTargets;
import rx.Observable;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
@ApplicationScoped
public class ClientAPIDemo {


    /**
     * A synchronous invocation encupsulated in a command.
     */
    @CircuitBreaker(fallbackMethod = "dateFallback", threadPoolKey = "firstPool")
    public String syncIsolatedCommand() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://date.jsontest.com/");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        Assert.assertEquals(200, response.getStatus());
        return response.readEntity(String.class);
    }

    /**
     * An asynchronous invocation with rx-based Observable responses, still encupsulated in a command.
     * Allows for scatter-gather scenarios and tight integration with rx-based invocations.
     */
    @CircuitBreaker(fallbackMethod = "dateFallback")
    public Observable<String> asyncIsolatedCommand() {

        // Here we wrap the invocation response on an Observable that will
        // trigger the invocation upon subcription, but that's just for demonstration purposes

        // A more useful example would be a forward invocation to anotehr rx-based API
        return Observable.create(subscriber -> {
            try {

                Client client = ClientBuilder.newClient();
                WebTarget target = client.target("http://date.jsontest.com/");

                Response response = target.request(MediaType.APPLICATION_JSON).get();
                Assert.assertEquals(200, response.getStatus());

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(response.readEntity(String.class));
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });

    }

    @Inject
    @ServerList(service = "date-service")
    private ServiceTargets<Server> serviceTargets;

    public String dynamicAddress() {
        Client client = ClientBuilder.newClient();

        Optional<WebTarget> target = serviceTargets.get().stream()
                .filter(Server::isAlive)
                .findFirst()
                .map(s -> {
                    return client.target(s.asHttp());
                });

        if(target.isPresent()) {
            Response response = target.get().request(MediaType.APPLICATION_JSON).get();
            Assert.assertEquals(200, response.getStatus());
            return response.readEntity(String.class);
        } else {
            throw new RuntimeException("No active servers found");
        }
    }

    /**
     * The fallback method for both approaches
     */
    public String dateFallback() {
        return "{\n" +
                "   \"time\": \"00:00:00 AM\",\n" +
                "   \"milliseconds_since_epoch\": unknown,\n" +
                "   \"date\": \"01-01-2000\"\n" +
                "}";
    }
}
