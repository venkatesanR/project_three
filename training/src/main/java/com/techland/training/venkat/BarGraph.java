package com.techland.training.venkat;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class BarGraph extends JComponent {
    private int width;
    private int height;
    private List<Integer> data;

    public BarGraph(int aWidth, int aHeight) {
        width = aWidth;
        height = aHeight;
        data = new ArrayList<Integer>();
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public void draw(Graphics2D g2) {
        int i = 0;
        double max = 0;

        for (Integer wrapper : data)
            if (max < wrapper)
                max = wrapper;

        int xwidth = width - 1;
        int yheight = height - 1;

        int xleft = 0;

        for (i = 0; i < data.size(); i++) {
            int xright = xwidth * (i + 1) / data.size();
            int barWidth = xwidth / data.size();
            int barHeight = (int) Math.round(yheight * data.get(i) / max);
            Rectangle bar = new Rectangle(xleft, yheight - barHeight, barWidth, barHeight);
            g2.draw(bar);
            xleft = xright;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        this.draw(g2);
    }

}
