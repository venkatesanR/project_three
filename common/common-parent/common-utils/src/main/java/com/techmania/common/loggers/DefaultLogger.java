package com.techmania.common.loggers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultLogger {
    private static final Logger logger = LogManager.getLogger(DefaultLogger.class);

    public void printDefault() {
        logger.debug("Hello Default {} | {} | {} ", new Object[]{1, 3, 4});
    }
}
