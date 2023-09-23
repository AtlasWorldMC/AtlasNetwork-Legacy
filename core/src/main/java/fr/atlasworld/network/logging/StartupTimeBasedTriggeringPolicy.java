package fr.atlasworld.network.logging;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RolloverFailure;

/**
 * Triggering policy for rolling logs file, roles files at startup
 * <p>
 * File from Crafted Launcher find the original file <a href="https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/misc/logging/StartupTimeBasedTriggeringPolicy.java">here</a>
 * @author RaftDev
 */
@NoAutoStart
public class StartupTimeBasedTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {
    @Override
    public void start() {
        super.start();
        atomicNextCheck.set(0L);
        isTriggeringEvent(null, null);
        try {
            tbrp.rollover();
        } catch (RolloverFailure e) {
            //Do nothing
        }
    }
}
