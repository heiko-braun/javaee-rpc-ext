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
 * This annotation used to specify some methods which should be processes as isolated commands.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@InterceptorBinding
public @interface CircuitBreaker {

    /**
     * The command group key is used for grouping together commands such as for reporting,
     * alerting, dashboards or team/library ownership.
     * <p/>
     * default => the runtime class name of annotated method
     *
     * @return group key
     */
    @Nonbinding
    String groupKey() default "";

    /**
     * Command key.
     * <p/>
     * default => the name of annotated method. for example:
     * <code>
     *     ...
     *     @IsolatedCommand
     *     public User getUserById(...)
     *     ...
     *     the command name will be: 'getUserById'
     * </code>
     *
     * @return command key
     */
    @Nonbinding
    String commandKey() default "";

    /**
     * The thread-pool key is used to represent a
     * ThreadPool for monitoring, metrics publishing, caching and other such uses.
     *
     * @return thread pool key
     */
    @Nonbinding
    String threadPoolKey() default "";

    /**
     * Specifies a method to process fallback logic.
     * A fallback method should be defined in the same class where is IsolatedCommand.
     * Also a fallback method should have same signature to a method which was invoked as an isolated command.
     * for example:
     * <code>
     *      @IsolatedCommand(fallbackMethod = "getByIdFallback")
     *      public String getById(String id) {...}
     *
     *      private String getByIdFallback(String id) {...}
     * </code>
     * Also a fallback method can be annotated with {@link CircuitBreaker}
     * <p/>
     *
     * @return method name
     */
    @Nonbinding
    String fallbackMethod() default "";



}
