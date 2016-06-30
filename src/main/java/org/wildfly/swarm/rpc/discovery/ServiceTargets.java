package org.wildfly.swarm.rpc.discovery;

import java.util.List;

public interface ServiceTargets<T> {

    List<T> get();

    long size();
}
