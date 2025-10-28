package com.demian.controller;

import com.demian.model.Plane;
import com.demian.view.GUIView;
import com.demian.view.Menu;

public class GUIController {

    private final Plane model;
    private GUIView view;

    public GUIController(Plane model, GUIView view) {
        this.model = model;
        this.view = view;

        setGridListeners();
        setMenuListeners(view.getMenuBar());
    }

    private void setGridListeners() {
        view.getGrid().setOnCellClicked((x, y) -> {
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

        menu.setOnAlternatingRulesetAdded(model::addAlternatingRuleSet);

        menu.setOnAlternatingRulesetRemoved(model::removeAlternatingRuleSet);
    }

    public void nextGeneration() {
        model.simulateGeneration();
        view.repaintGrid();
    }
}
