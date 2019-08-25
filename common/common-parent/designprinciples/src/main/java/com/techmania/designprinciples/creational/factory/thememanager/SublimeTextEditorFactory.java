package com.techmania.designprinciples.creational.factory.thememanager;

import com.techmania.designprinciples.creational.factory.thememanager.theme.IWidget;
import com.techmania.designprinciples.creational.factory.thememanager.theme.Themes;

public class SublimeTextEditorFactory {
    private IWidget widgetManager;

    public SublimeTextEditorFactory(Themes themes) {
        if (themes == Themes.MONKAI) {
            widgetManager = new MonakaiManager();
        } else if (themes == Themes.CLASSIC) {
            widgetManager = new ClassicManager();
        }
    }

    public IWidget getWidgetManager() {
        return widgetManager;
    }
}
