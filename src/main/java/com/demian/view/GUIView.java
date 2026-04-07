package com.demian.view;

import com.demian.model.Plane;

import javax.swing.*;
import java.awt.*;

public class GUIView {
    private JFrame frame;
    private Grid grid;
    private Menu menuBar;

    public GUIView(Plane plane) {
        frame = new JFrame("Cellular Automata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        grid = new Grid(plane);
        frame.add(grid, BorderLayout.CENTER);

        menuBar = new Menu(grid);
        frame.setJMenuBar(menuBar);

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }

    public Grid getGrid() {
        return grid;
    }

    public Menu getMenuBar() {
        return menuBar;
    }

    public void repaintGrid() {
        grid.repaint();
    }
}
