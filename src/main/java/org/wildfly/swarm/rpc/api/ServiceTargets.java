package org.wildfly.swarm.rpc.api;

import java.util.List;

public interface ServiceTargets<T> {

    List<T> current();

    long size();
}
