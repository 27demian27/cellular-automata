package com.demian.view;

import com.demian.model.Plane;
import com.demian.view.menu.Menu;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;


public class GUIView {

    private final JFrame frame;

    @Getter
    private final Grid grid;
    @Getter
    private final Menu menuBar;

    public GUIView(Plane plane) {
        frame = new JFrame("Cellular Automata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        grid = new Grid(plane);
        grid.configure();
        frame.add(grid, BorderLayout.CENTER);

        menuBar = new Menu(frame, plane, grid);
        frame.setJMenuBar(menuBar);

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void resizeGrid(int x, int y) {
        grid.translate(x, y);
        grid.resizeBufferedImage();
    }

    public void repaintGrid() {
        grid.buildBufferedImage();
        grid.repaint();
    }
}
