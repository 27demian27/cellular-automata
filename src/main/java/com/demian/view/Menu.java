package com.demian.view;

import com.demian.controller.GUIController;
import com.demian.model.Plane;
import com.demian.model.RuleSet;

import javax.swing.*;
import java.util.function.Consumer;

public class Menu extends JMenuBar {

    private Runnable onClearRequested;
    private Runnable onNextGenerationRequested;
    private Consumer<RuleSet> onRuleSetSelected;

    public Menu() {
        addRuleSetMenu();
        addEditMenu();
    }

    private void addRuleSetMenu() {
        JMenu ruleSetMenu = new JMenu("RULESET");

        for (RuleSet ruleSet : RuleSet.values()) {
            JMenuItem ruleSetItem = new JMenuItem(ruleSet.name());
            ruleSetItem.addActionListener(e -> {
                if (onRuleSetSelected != null)
                    onRuleSetSelected.accept(ruleSet);
                ruleSetMenu.setText(ruleSet.name());
            });
            ruleSetMenu.add(ruleSetItem);
        }

        add(ruleSetMenu);
    }

    private void addEditMenu() {
        JMenu editMenu = new JMenu("EDIT");

        JMenuItem clearItem = new JMenuItem("Clear Grid");
        clearItem.addActionListener(e -> {
            if (onClearRequested != null)
                onClearRequested.run();
        });
        editMenu.add(clearItem);

        add(editMenu);
    }

    public void setOnClearRequested(Runnable onClearRequested) {
        this.onClearRequested = onClearRequested;
    }

    public void setOnNextGenerationRequested(Runnable onNextGenerationRequested) {
        this.onNextGenerationRequested = onNextGenerationRequested;
    }

    public void setOnRuleSetSelected(Consumer<RuleSet> onRuleSetSelected) {
        this.onRuleSetSelected = onRuleSetSelected;
    }
}
