package com.demian.view;

import com.demian.model.Plane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Grid extends JPanel {

    private Plane plane;
    private double scale = 1.0;
    private int translateX = 0;
    private int translateY = 0;
    private Point lastDragPoint = null;

    private BiConsumer<Integer, Integer> onCellClicked;

    private Runnable onNextGenerationRequested;

    public Grid(Plane plane) {
        this.plane = plane;

        setBackground(Color.DARK_GRAY);

        // Panning
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) lastDragPoint = e.getPoint();
                else if (SwingUtilities.isLeftMouseButton(e)) handleCellClick(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && lastDragPoint != null) {
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;
                    translateX += dx;
                    translateY += dy;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        // Zooming
        addMouseWheelListener(e -> {
            double oldScale = scale;
            double factor = 1.1;
            int notches = e.getWheelRotation();
            if (notches < 0)
                scale *= Math.pow(factor, -notches);
            else
                scale /= Math.pow(factor, notches);

            scale = Math.max(0.05, Math.min(10.0, scale));

            Point p = e.getPoint();
            translateX = (int) (p.x - (p.x - translateX) * (scale / oldScale));
            translateY = (int) (p.y - (p.y - translateY) * (scale / oldScale));

            repaint();
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "nextGen");
        getActionMap().put("nextGen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onNextGenerationRequested != null)
                    onNextGenerationRequested.run();
            }
        });
    }

    private void handleCellClick(MouseEvent e) {
        int cellSize = 20;

        double invScale = 1.0 / scale;
        int logicalX = (int) ((e.getX() - translateX) * invScale / cellSize);
        int logicalY = (int) ((e.getY() - translateY) * invScale / cellSize);

        if (logicalX >= 0 && logicalX < plane.getSizeX() && logicalY >= 0 && logicalY < plane.getSizeY()) {
            if (onCellClicked != null)
                onCellClicked.accept(logicalX, logicalY);
        }
    }

    public void setOnCellClicked(BiConsumer<Integer, Integer> listener) {
        this.onCellClicked = listener;
    }

    public void setOnNextGenerationRequested(Runnable listener) {
        this.onNextGenerationRequested = listener;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plane == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.translate(translateX, translateY);
        g2.scale(scale, scale);

        int cellSize = 20;
        double minScaleForBorderDraw = 0.10;

        Rectangle clip = g2.getClipBounds();
        int startX = Math.max(0, clip.x / cellSize);
        int endX   = Math.min(plane.getSizeX(), (clip.x + clip.width) / cellSize + 1);
        int startY = Math.max(0, clip.y / cellSize);
        int endY   = Math.min(plane.getSizeY(), (clip.y + clip.height) / cellSize + 1);

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int state = plane.getState(x, y).orElse(0);
                g2.setColor(state == 1 ? Color.BLACK : Color.WHITE);
                g2.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                if (scale > minScaleForBorderDraw) {
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        g2.dispose();
    }
}
