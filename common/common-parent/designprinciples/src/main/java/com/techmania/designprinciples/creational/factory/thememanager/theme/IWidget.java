package com.techmania.designprinciples.creational.factory.thememanager.theme;

public interface IWidget {
    public Window createWindow();

    public ScrollBar createScrollBar();
}
