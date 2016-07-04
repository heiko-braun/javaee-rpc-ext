package org.wildfly.swarm.rpc;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.wildfly.swarm.rpc.demo.DemoDriverBean;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        try {
            container.select(DemoDriverBean.class).get().begin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Press any key to terminate ...");

        System.in.read();
        container.shutdown();

    }
}
