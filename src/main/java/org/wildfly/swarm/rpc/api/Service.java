package org.wildfly.swarm.rpc.api;

import java.net.URI;

/**
 * Represent and addressable service
 *
 * @author Heiko Braun
 * @since 05/07/16
 */
public interface Service {

    String getHost();

    int getPort();

    URI asHttp();

    String getId();

    boolean isAlive();
}
