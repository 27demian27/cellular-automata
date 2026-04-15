package com.demian.controller;

import com.demian.model.Plane;
import com.demian.view.GUIView;
import com.demian.view.menu.Menu;

import java.io.IOException;


public class GUIController {

    private final SaveController saveController;

    private final Plane model;
    private GUIView view;

    public GUIController(SaveController saveController, Plane model, GUIView view) {
        this.saveController = saveController;
        this.model = model;
        this.view = view;

        setGridListeners();
        setMenuListeners(view.getMenuBar());
    }

    private void setGridListeners() {
        view.getGrid().setOnCellToggled((x, y) -> {
            int current = model.getState(x, y).orElse(0);
            model.setState(current == 0 ? 1 : 0, x, y);
            view.repaintGrid();
        });

        view.getGrid().setOnNextGenerationRequested(this::nextGeneration);
    }

    private void setMenuListeners(Menu menu) {
        menu.setOnClearRequested(() -> {
            model.killCells();
            view.repaintGrid();
        });

        menu.setOnRandomizeRequested(() -> {
            model.randomizeCells();
            view.repaintGrid();
        });

        menu.setOnNextGenerationRequested(this::nextGeneration);

        menu.setOnRuleSetSelected(ruleSet -> {
            model.setCurrentRuleSet(ruleSet);
            view.repaintGrid();
        });

        menu.setOnCustomRulesetChanged(model::setCustomRuleSet);

        menu.setOnAlternatingRulesetAdded(model::addAlternatingRuleSet);

        menu.setOnAlternatingRulesetRemoved(model::removeAlternatingRuleSet);

        menu.setOnStateSaveRequested(() -> {
            try {
                saveController.saveState();
            } catch (IOException e) {
                menu.showErrorDialog(e);
            }
        });

        menu.setOnStateLoadRequested(name -> {
            try {
                saveController.loadState(name);
            } catch (IOException e) {
                menu.showErrorDialog(e);
            }
        });
    }

    public void nextGeneration() {
        model.simulateGeneration();
        view.repaintGrid();
    }
}
