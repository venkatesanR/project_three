package com.techmania.designprinciples.creational.factory.thememanager.theme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassicScrollBar implements ScrollBar {
    private static final Logger logger = LoggerFactory.getLogger(ClassicScrollBar.class);

    @Override
    public void scroll(MoveAction moveAction) {
        logger.info("Classic scrollbar moved to location: [{}]", moveAction.name());
    }
}
