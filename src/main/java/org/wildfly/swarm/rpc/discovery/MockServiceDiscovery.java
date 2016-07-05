package org.wildfly.swarm.rpc.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.wildfly.swarm.rpc.api.Service;
import org.wildfly.swarm.rpc.api.ServiceDiscovery;
import org.wildfly.swarm.rpc.api.ServiceTargets;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
@Singleton
public class MockServiceDiscovery implements org.wildfly.swarm.rpc.discovery.ServiceDiscovery {

    // the registry is subject to change and requires thread safe access
    private Map<String, Set<Service>> registry = new ConcurrentHashMap<>();

    private TestDriver testDriver;

    @PostConstruct
    private void init() {
        Set<Service> dateAddresses = new HashSet<>();
        dateAddresses.add(ServiceImpl.parseId("http://date.jsontest.com/?s=1"));
        dateAddresses.add(ServiceImpl.parseId("http://date.jsontest.com/?s=2"));
        dateAddresses.add(ServiceImpl.parseId("http://date.jsontest.com/?s=3"));
        dateAddresses.add(ServiceImpl.parseId("http://date.jsontest.com/?s=4"));
        registry.put("date-service", dateAddresses);

        testDriver = new TestDriver(() -> {
            registry.get("date-service").forEach(s -> {
                // shuffle service availability
                ((ServiceImpl)s).setAlive(Math.random() < 0.5);
            });
        });

        testDriver.start();
    }

    class TestDriver extends Thread {
        private final Runnable r;

        boolean runnning = true;

        public TestDriver(Runnable r) {
            this.r= r;
        }

        public void setRunnning(boolean runnning) {
            this.runnning = runnning;
        }

        @Override
        public void run() {
            while(runnning) {
                r.run();
                try {
                    Thread.sleep(75);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @PreDestroy
    private void teardown() {
        testDriver.setRunnning(false);
    }


    public ServiceTargets<Service> getServiceTargets(String serviceName) {
        ServiceTargets<Service> targets = targetFrom(registry, serviceName);
        System.out.printf("Resolved %s addresses for service '%s'\n", targets.size(), serviceName);
        return targets;
    }

    private ServiceTargets<Service> targetFrom(final Map<String, Set<Service>> registry, final String serviceName) {
        return new ServiceTargets<Service>() {

            @Override
            public List<Service> current() {

                // effectivle each access to current() recreates the list
                List<Service> servers = Collections.EMPTY_LIST;
                if(registry.containsKey(serviceName)) {
                    Set<Service> services = registry.get(serviceName);
                    servers = new ArrayList<>(services.size());
                    for (Service service : services) {
                        servers.add(service);
                    }
                }
                return Collections.unmodifiableList(servers);
            }

            @Override
            public long size() {
                return current().size();
            }
        };
    }

    @Produces
    @ServiceDiscovery
    ServiceTargets<Service> createServerList(InjectionPoint ip) {
        return getServiceTargets(
                ip.getAnnotated().getAnnotation(ServiceDiscovery.class).service()
        );
    }


}
