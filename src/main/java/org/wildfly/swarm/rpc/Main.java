package org.wildfly.swarm.rpc;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.wildfly.swarm.rpc.demo.DemoBean;

/**
 * @author Heiko Braun
 * @since 29/06/16
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Weld weld = new Weld();


        try (WeldContainer container = weld.initialize()) {

            container.select(DemoBean.class).get().begin();

        }

    }
}
