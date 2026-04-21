package com.demian.view;

import com.demian.model.Plane;
import com.demian.util.Bounds;
import com.demian.view.painting.PaintMode;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.*;
import java.util.function.BiConsumer;

public class Grid extends JPanel {

    @Getter
    private final Plane plane;

    private double scale;

    @Getter
    private int translateX;
    @Getter
    private int translateY;

    private Point lastDragPoint;
    private Point lastGridPaintPoint;

    @Getter
    private Bounds viewBounds;
    private final Deque<Map<Point, Integer>> recentlyPaintedPoints;
    @Setter
    private PaintMode paintMode;
    private boolean showGridLines;

    @Getter
    private boolean showDebug;
    @Setter
    private BiConsumer<Integer, Integer> onCellToggled;
    @Setter
    private Runnable onNextGenerationRequested;

    private DebugDrawer debugDrawer;

    private BufferedImage buffer;

    public final static int cellSize = 20;
    public final static double minScaleForBorderDraw = 0.2;

    public Grid(Plane plane) {
        this.plane = plane;
        scale = 1.0;
        translateX = 0;
        translateY = 0;
        paintMode = PaintMode.NORMAL;
        recentlyPaintedPoints = new LinkedList<>();
        lastDragPoint = new Point(-1, -1);
        lastGridPaintPoint = new Point(-1, -1);
        showGridLines = true;
        showDebug = false;
    }

    public void configure() {
        setBackground(Color.DARK_GRAY);
        setLayout(new OverlayLayout(this));
        configureDebugDrawer();
        configureControls();
    }

    private void configureDebugDrawer() {
        debugDrawer = new DebugDrawer(this);
        debugDrawer.setVisible(true);
        debugDrawer.setOpaque(false);
        debugDrawer.setBounds(0, 0, getWidth(), getHeight());

        add(debugDrawer);
    }

    private void configureControls() {
        // Panning
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private final Cursor defaultCursor =  new Cursor(Cursor.DEFAULT_CURSOR);
            private final Cursor panningCursor =  new Cursor(Cursor.MOVE_CURSOR);
            private final Cursor brushCursor   =  new Cursor(Cursor.CROSSHAIR_CURSOR);


            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    recentlyPaintedPoints.push(new HashMap<>());
                    handleCellClick(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
                setCursor(defaultCursor);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (showDebug) {
                    debugDrawer.setMousePoint(e.getPoint());
                    debugDrawer.setMouseGridPoint(toGridPoint(e.getPoint()));
                    debugDrawer.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint == null) return;


                if (SwingUtilities.isRightMouseButton(e)) {
                    setCursor(panningCursor);
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;
                    translateX += dx;
                    translateY += dy;
                    lastDragPoint = e.getPoint();
                    repaint();
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    setCursor(brushCursor);
                    if (lastDragPoint != e.getPoint()) {
                        lastDragPoint = e.getPoint();
                        handleCellPaintDrag(e);
                    }
                }

                if (showDebug) {
                    debugDrawer.setMousePoint(e.getPoint());
                    debugDrawer.setMouseGridPoint(toGridPoint(e.getPoint()));
                    debugDrawer.repaint();
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

            scale = Math.clamp(scale, 0.005, 10.0);

            Point p = e.getPoint();
            translateX = (int) (p.x - (p.x - translateX) * (scale / oldScale));
            translateY = (int) (p.y - (p.y - translateY) * (scale / oldScale));

            repaint();
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "nextGen");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undo");
        getActionMap().put("nextGen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onNextGenerationRequested != null)
                    onNextGenerationRequested.run();
            }
        });
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                undoRecentPaint();
            }
        });
    }

    private Point toGridPoint(Point mousePoint) {
        int cellSize = 20;

        double invScale = 1.0 / scale;
        int logicalX = (int) ((mousePoint.getX() - translateX) * invScale / cellSize);
        int logicalY = (int) ((mousePoint.getY() - translateY) * invScale / cellSize);

        if (logicalX < 0 && logicalX >= plane.getSizeX() && logicalY < 0 && logicalY >= plane.getSizeY())
            throw new RuntimeException("mousePoint outside of grid");

        return new Point(logicalX, logicalY);
    }

    private void handleCellClick(MouseEvent e) {
        Point gridPoint = toGridPoint(e.getPoint());

        if (!plane.checkBounds(gridPoint.x, gridPoint.y))
            return;

        Map<Point, Integer> gridState = recentlyPaintedPoints.peek();
        if (gridState != null) {
                gridState.put(gridPoint, plane.getCell(gridPoint.x, gridPoint.y).state);
        }

        if (onCellToggled != null) {
            onCellToggled.accept(gridPoint.x, gridPoint.y);
        }

    }

    private void handleCellPaintDrag(MouseEvent e) {
        Point gridPoint = toGridPoint(e.getPoint());

        if (!plane.checkBounds(gridPoint.x, gridPoint.y))
            return;

        if (!lastGridPaintPoint.equals(gridPoint)) {
            lastGridPaintPoint = gridPoint;
            if (plane.getCell(gridPoint.x, gridPoint.y).state == 1 && paintMode == PaintMode.ERASE) {
                handleCellClick(e);
            }
            if (plane.getCell(gridPoint.x, gridPoint.y).state != 1 && paintMode == PaintMode.NORMAL) {
                handleCellClick(e);
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plane == null) return;

        if (buffer == null) {
            buffer = new BufferedImage(plane.getSizeX()*cellSize, plane.getSizeY()*cellSize, BufferedImage.TYPE_BYTE_GRAY);
            buildBufferedImage();
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.translate(translateX, translateY);
        g2.scale(scale, scale);
        g2.drawImage(buffer, 0, 0, null);


        Rectangle clip = g2.getClipBounds();
        int startX = Math.max(0, clip.x / cellSize);
        int endX   = Math.min(plane.getSizeX(), (clip.x + clip.width) / cellSize + 1);
        int startY = Math.max(0, clip.y / cellSize);
        int endY   = Math.min(plane.getSizeY(), (clip.y + clip.height) / cellSize + 1);

        viewBounds = new Bounds(startX, endX, startY, endY);

        if (showGridLines && scale > minScaleForBorderDraw) {
            g2.setColor(Color.BLACK);

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    g2.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public void resizeBufferedImage() {
        buffer = new BufferedImage(plane.getSizeX()*cellSize, plane.getSizeY()*cellSize, BufferedImage.TYPE_BYTE_GRAY);
        buildBufferedImage();
    }

    public void buildBufferedImage() {
        Graphics2D g2 = buffer.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, plane.getSizeX() * cellSize, plane.getSizeY() * cellSize);

        for (int x = 0; x < plane.getSizeX(); x++) {
            for (int y = 0; y < plane.getSizeY(); y++) {
                int state = plane.getState(x, y).orElse(0);
                if (state == 1) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        g2.dispose();

    }

    public void undoRecentPaint() {
        Map<Point, Integer> gridState = recentlyPaintedPoints.poll();
        if (gridState == null) return;

        for (Map.Entry<Point, Integer> entry : gridState.entrySet()) {
            Point p = entry.getKey();
            plane.getCell(p.x, p.y).state = entry.getValue();
        }

        repaint();
    }

    public void toggleGridLines() {
        showGridLines = !showGridLines;
        repaint();
    }

    public void toggleShowDebug() {
        showDebug = !showDebug;
        repaint();
    }

    public void translate(int translateX, int translateY) {
        this.translateX += translateX;
        this.translateY += translateY;
    }

}
