package com.techmania.common.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public abstract class AbstractDriver {
    public abstract void setLogLevel(Level level);

    private void initLogging() {
        Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }
}
