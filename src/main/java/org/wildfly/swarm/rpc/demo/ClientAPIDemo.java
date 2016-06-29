package org.wildfly.swarm.rpc.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.wildfly.swarm.rpc.api.IsolatedCommand;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
@ApplicationScoped
public class ClientAPIDemo {


    @IsolatedCommand(fallbackMethod = "dateFallback")
    public String syncIsolatedCommand() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://date.jsontest.com/");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        Assert.assertEquals(200, response.getStatus());
        return response.readEntity(String.class);
    }

    @IsolatedCommand(fallbackMethod = "dateFallback")
    public Observable<String> asyncIsolatedCommand() {

        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
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
            }
        });

    }

    public String dateFallback() {
        return "{\n" +
                "   \"time\": \"00:00:00 AM\",\n" +
                "   \"milliseconds_since_epoch\": unknown,\n" +
                "   \"date\": \"01-01-2000\"\n" +
                "}";
    }
}
