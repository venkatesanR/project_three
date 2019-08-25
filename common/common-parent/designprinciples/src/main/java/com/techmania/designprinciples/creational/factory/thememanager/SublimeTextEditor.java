package com.techmania.designprinciples.creational.factory.thememanager;

import com.techmania.designprinciples.creational.factory.thememanager.theme.ClassicWindow;
import com.techmania.designprinciples.creational.factory.thememanager.theme.IWidget;
import com.techmania.designprinciples.creational.factory.thememanager.theme.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SublimeTextEditor {
    private static final Logger logger = LoggerFactory.getLogger(ClassicWindow.class);

    private SublimeTextEditorFactory factory;

    public SublimeTextEditor(Themes themes) {
        if (factory == null) {
            factory = new SublimeTextEditorFactory(themes);
        }
    }

    private SublimeTextEditorFactory getFactory() {
        return factory;
    }

    public static void main(String[] args) {
        SublimeTextEditor sublimeTextEditor = new SublimeTextEditor(Themes.MONKAI);
        IWidget widget = sublimeTextEditor.getFactory().getWidgetManager();
        widget.createWindow().moveToLocation(340, 900);
        System.out.println(widget.createWindow().currentPointer().getY());
    }
}
