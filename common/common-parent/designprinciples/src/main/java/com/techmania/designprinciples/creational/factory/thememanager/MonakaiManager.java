package com.techmania.designprinciples.creational.factory.thememanager;

import com.techmania.designprinciples.creational.factory.thememanager.theme.IWidget;
import com.techmania.designprinciples.creational.factory.thememanager.theme.MonakaiScrollBar;
import com.techmania.designprinciples.creational.factory.thememanager.theme.MonakaiWindow;
import com.techmania.designprinciples.creational.factory.thememanager.theme.ScrollBar;
import com.techmania.designprinciples.creational.factory.thememanager.theme.Window;

public class MonakaiManager implements IWidget {
    @Override
    public Window createWindow() {
        return new MonakaiWindow();
    }

    @Override
    public ScrollBar createScrollBar() {
        return new MonakaiScrollBar();
    }
}
