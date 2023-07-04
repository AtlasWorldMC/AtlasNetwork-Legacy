package fr.atlasworld.network.logging;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RolloverFailure;
import fr.atlasworld.network.AtlasNetwork;

//Author: RaftDev
//Find the original file here : https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/misc/logging/StartupTimeBasedTriggeringPolicy.java
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
