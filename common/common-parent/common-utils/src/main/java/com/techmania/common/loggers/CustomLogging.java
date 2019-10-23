package com.techmania.common.loggers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomLogging {
    private static final Logger componentLogger = LogManager.getLogger(CustomLogging.class);

    public void printCustom(String data) {
        componentLogger.debug("Hello Custom ${}", componentLogger.getLevel());
    }

    public static void main(String[] args) {
        CustomLogging ref2 = new CustomLogging();
        DefaultLogger ref1 = new DefaultLogger();
        ref1.printDefault();
        ref2.printCustom("nd");
    }
}
