package com.techmania.designprinciples.creational.factory.thememanager.theme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonakaiWindow implements Window {
    private static final Logger logger = LoggerFactory.getLogger(MonakaiWindow.class);

    private CursorLocation cursorLocation;

    public MonakaiWindow() {
        cursorLocation = new CursorLocation(0, 0);
    }

    @Override
    public void moveToLocation(int x, int y) {
        logger.error("Cursor location in monakai window changed from [{},{}] -> [{},{}]", cursorLocation.getX(),
                cursorLocation.getY(), x, y);
        cursorLocation = new CursorLocation(x, y);
    }

    @Override
    public CursorLocation currentPointer() {
        return cursorLocation;
    }
}
