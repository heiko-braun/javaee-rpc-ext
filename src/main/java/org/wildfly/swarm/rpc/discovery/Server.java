package org.wildfly.swarm.rpc.discovery;

/**
 * @author Heiko Braun
 * @since 30/06/16
 */
public class Server {
    private String host;
    private int port = 80;
    private String id;
    private boolean isAliveFlag;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.id = host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }

    public void setAlive(boolean isAliveFlag) {
        this.isAliveFlag = isAliveFlag;
    }

    public boolean isAlive() {
        return isAliveFlag;
    }

    static Pair<String, Integer> parseId(String id) {
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
            return new Pair(host, port);
        } else {
            return null;
        }

    }
}
