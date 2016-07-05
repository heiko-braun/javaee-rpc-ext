package org.wildfly.swarm.rpc.discovery;

import java.net.URI;

import org.wildfly.swarm.rpc.api.Service;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
class ServiceImpl implements Service {

    private String host;
    private int port = 80;
    private String id;
    private boolean isAliveFlag;

    public ServiceImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.id = host + ":" + port;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public URI asHttp() {
        return URI.create("http://"+getHost()+":"+getPort());
    }

    @Override
    public String getId() {
        return id;
    }

    public ServiceImpl setAlive(boolean isAliveFlag) {
        this.isAliveFlag = isAliveFlag;
        return this;
    }

    @Override
    public boolean isAlive() {
        return isAliveFlag;
    }

    static Service parseId(String id) {
        if (id != null) {
            String host = null;
            int port = 80;

            if (id.toLowerCase().startsWith("http://")) {
                id = id.substring(7);
            } else if (id.toLowerCase().startsWith("https://")) {
                id = id.substring(8);
            }

            if (id.contains("/")) {
                int slash_idx = id.indexOf("/");
                id = id.substring(0, slash_idx);
            }

            int colon_idx = id.indexOf(':');

            if (colon_idx == -1) {
                host = id; // default
                port = 80;
            } else {
                host = id.substring(0, colon_idx);
                try {
                    port = Integer.parseInt(id.substring(colon_idx + 1));
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
            return new ServiceImpl(host, port);
        } else {
            return null;
        }

    }
}
