package com.techmania.designprinciples.creational.factory.thememanager;

import com.techmania.designprinciples.creational.factory.thememanager.theme.ClassicScrollBar;
import com.techmania.designprinciples.creational.factory.thememanager.theme.ClassicWindow;
import com.techmania.designprinciples.creational.factory.thememanager.theme.IWidget;
import com.techmania.designprinciples.creational.factory.thememanager.theme.ScrollBar;
import com.techmania.designprinciples.creational.factory.thememanager.theme.Window;

public class ClassicManager implements IWidget {
    @Override
    public Window createWindow() {
        return new ClassicWindow();
    }

    @Override
    public ScrollBar createScrollBar() {
        return new ClassicScrollBar();
    }
}
