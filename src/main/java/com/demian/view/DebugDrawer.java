package com.demian.view;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class DebugDrawer extends JComponent {

    private final static int coordsTextSpacingX = 6;
    private final static int coordsTextSpacingY = -6;

    private final Grid grid;

    @Setter
    private Point mouseGridPoint;
    @Setter
    private Point mousePoint;

    private final static Font coordsFont = new Font("Monospaced", Font.BOLD, 14);
    private final static Font gridSizeFont = new Font("Monospaced", Font.BOLD, 24);
    private final static Font planeStateFont = new Font("Monospaced", Font.BOLD, 18);



    public DebugDrawer(Grid grid) {
        this.grid = grid;
        this.mouseGridPoint = new Point(0, 0);
        this.mousePoint = new Point(0, 0);
    }

    private void drawCoords(Graphics2D g2) {
        g2.setFont(coordsFont);
        g2.setColor(Color.RED);
        g2.drawString(
                "["+mouseGridPoint.x+", "+mouseGridPoint.y+"]",
                mousePoint.x + coordsTextSpacingX,
                mousePoint.y + coordsTextSpacingY
        );
    }


    private void drawGridMetrics(Graphics2D g2) {
        g2.setFont(gridSizeFont);
        g2.setColor(Color.RED);

        String gridSizeString = grid.getPlane().getSizeX() +" x "+grid.getPlane().getSizeY();
        int stringWidth = g2.getFontMetrics().stringWidth(gridSizeString);
        int stringHeight = g2.getFontMetrics().getHeight();
        g2.drawString(
                gridSizeString,
                grid.getWidth()-stringWidth-10,
                grid.getHeight()-10
        );

        g2.setFont(planeStateFont);
        g2.setColor(Color.RED);

        String aliveCellCountString = "Alive: " + grid.getPlane().getAliveCellCount();
        String deadCellCountString = "Dead: " + (grid.getPlane().getSizeX() * grid.getPlane().getSizeY() - grid.getPlane().getAliveCellCount());
        int aliveStringWidth = g2.getFontMetrics().stringWidth(aliveCellCountString);
        int deadStringWidth = g2.getFontMetrics().stringWidth(deadCellCountString);
        int metricsStringHeight = g2.getFontMetrics().getHeight();
        int alignPos = Math.max(aliveStringWidth, deadStringWidth);

        g2.drawString(deadCellCountString, grid.getWidth() - alignPos - 10, grid.getHeight() - stringHeight - 10);
        g2.drawString(aliveCellCountString, grid.getWidth() - alignPos - 10, grid.getHeight() - stringHeight - metricsStringHeight - 10);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!grid.isShowDebug())
            return;

        Graphics2D g2 = (Graphics2D) g;

        drawCoords(g2);
        drawGridMetrics(g2);
    }

}
