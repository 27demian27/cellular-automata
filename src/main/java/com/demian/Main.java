package com.demian;

import com.demian.model.Plane;
import com.demian.view.GUIView;
import com.demian.controller.GUIController;

import javax.swing.*;

public class Main {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Plane model = new Plane();
            model.initialize(400, 400);

            GUIView view = new GUIView(model);
            GUIController controller = new GUIController(model, view);

            view.show();
        });

    }
}