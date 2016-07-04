package org.wildfly.swarm.rpc.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;


/**
 * This annotation used to inject server lists for services
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@InterceptorBinding
public @interface ServiceDiscovery {

    /**
     * The name of the service
     */
    @Nonbinding
    String service() default  "";
}
