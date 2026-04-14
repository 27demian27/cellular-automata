package com.demian.view;

import javax.swing.*;
import java.awt.*;

public class DebugDrawer extends JComponent {

    private final static int coordsTextMarginX = 6;
    private final static int coordsTextMarginY = -6;

    private final Grid grid;

    private Point mouseGridPoint;
    private Point mousePoint;

    public DebugDrawer(Grid grid) {
        this.grid = grid;
        this.mouseGridPoint = new Point(0, 0);
        this.mousePoint = new Point(0, 0);
        this.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    private void drawCoords(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawString(
                "["+mouseGridPoint.x+", "+mouseGridPoint.y+"]",
                mousePoint.x + coordsTextMarginX,
                mousePoint.y + coordsTextMarginY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        drawCoords(g2);
    }

    public void setMouseGridPoint(Point mouseGridPoint) {
        this.mouseGridPoint = mouseGridPoint;
    }

    public void setMousePoint(Point mousePoint) {
        this.mousePoint = mousePoint;
    }
}
