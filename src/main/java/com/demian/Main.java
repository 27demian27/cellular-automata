package com.demian;

import com.demian.controller.SaveController;
import com.demian.model.Plane;
import com.demian.view.GUIView;
import com.demian.controller.GUIController;

import javax.swing.*;

public class Main {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Plane model = new Plane();
            model.initialize(200, 200);

            GUIView view = new GUIView(model);
            SaveController saveController = new SaveController(model);
            GUIController controller = new GUIController(saveController, model, view);

            view.show();
        });

    }
}