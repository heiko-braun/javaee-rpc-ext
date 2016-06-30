package org.wildfly.swarm.rpc.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.wildfly.swarm.rpc.api.ServerList;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
@Singleton
public class MockServiceDiscovery implements ServiceDiscovery {

    private final static ServiceTargets<Server> EMPTY_SERVICES = new ServiceTargets<Server>() {
        @Override
        public List<Server> get() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public long size() {
            return 0;
        }
    };

    private Map<String, Set<String>> registry = new HashMap<>();

    @PostConstruct
    private void init() {
        Set<String> dateAddresses = new HashSet<>();
        dateAddresses.add("http://date.jsontest.com/?s=1");
        dateAddresses.add("http://date.jsontest.com/?s=2");
        dateAddresses.add("http://date.jsontest.com/?s=3");
        registry.put("date-service", dateAddresses);
    }

    public ServiceTargets<Server> getServiceAddresses(String serviceName) {
        ServiceTargets<Server> targets = registry.containsKey(serviceName) ? targetFrom(registry.get(serviceName)) : EMPTY_SERVICES;
        System.out.printf("Resolved %s addresses for service '%s'\n", targets.size(), serviceName);
        return targets;
    }

    @Produces
    @ServerList
    ServiceTargets<Server> createServerList(InjectionPoint ip) {
        return getServiceAddresses(
                ip.getAnnotated().getAnnotation(ServerList.class).service()
        );
    }

    private ServiceTargets<Server> targetFrom(final Set<String> addresses) {

        return new ServiceTargets<Server>() {

            @Override
            public List<Server> get() {
                ArrayList<Server> servers = new ArrayList<>(addresses.size());
                addresses.forEach(a -> {
                    Pair<String, Integer> pair = Server.parseId(a);
                    servers.add(
                            new Server(pair.first(), pair.second()).setAlive(true)
                    );
                });
                return servers;
            }

            @Override
            public long size() {
                return get().size();
            }
        };
    }
}
