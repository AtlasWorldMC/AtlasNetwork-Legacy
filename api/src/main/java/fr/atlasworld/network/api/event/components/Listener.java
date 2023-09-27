package fr.atlasworld.network.api.event.components;

import fr.atlasworld.network.api.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Listener {
    /**
     * Should ignore the listener not be triggered if the event is already cancelled?
     * If set to true, the listener shall not be triggered
     */
    boolean ignoreIfCancelled() default false;

    /**
     * Sets the priority of the listener, Higher priority will make it run before ones with lower priority
     */
    Priority priority() default Priority.NORMAL;
}
