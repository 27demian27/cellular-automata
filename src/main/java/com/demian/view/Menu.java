package com.demian.view;

import com.demian.controller.GUIController;
import com.demian.model.Plane;
import com.demian.model.RuleSet;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Menu extends JMenuBar {

    private Runnable onClearRequested;


    private Runnable onNextGenerationRequested;

    private Runnable onRandomizeRequested;


    private Consumer<RuleSet> onAlternatingRulesetAdded;
    private Consumer<RuleSet> onAlternatingRulesetRemoved;
    private Consumer<RuleSet> onRuleSetSelected;

    public Menu() {
        addRuleSetMenu();
        addEditMenu();
    }

    private void addRuleSetMenu() {
        JMenu ruleSetMenu = new JMenu("RULESET");

        for (RuleSet ruleSet : RuleSet.values()) {
            if (ruleSet == RuleSet.ALTERNATING)
                continue;

            JMenuItem ruleSetItem = new JMenuItem(ruleSet.name());
            ruleSetItem.addActionListener(e -> {
                if (onRuleSetSelected != null)
                    onRuleSetSelected.accept(ruleSet);
                ruleSetMenu.setText(ruleSet.name());
            });
            ruleSetMenu.add(ruleSetItem);
        }

        class MyCheckBoxMenuItem extends JCheckBoxMenuItem {

            MyCheckBoxMenuItem(String text) {
                super(text);
            }

            @Override
            protected void processMouseEvent(MouseEvent evt) {
                if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                    doClick();
                    setArmed(true);
                } else {
                    super.processMouseEvent(evt);
                }
            }
        }

        JMenu alternatingSelectionMenu = new JMenu("ALTERNATING");
        for (RuleSet ruleSet : RuleSet.values()) {
            if (ruleSet == RuleSet.ALTERNATING)
                continue;

            MyCheckBoxMenuItem ruleSetItem = new MyCheckBoxMenuItem(ruleSet.name());
            ruleSetItem.addActionListener(e -> {

                MyCheckBoxMenuItem item = (MyCheckBoxMenuItem) e.getSource();
                boolean isSelected = item.isSelected();

                if (onAlternatingRulesetAdded != null && isSelected)
                    onAlternatingRulesetAdded.accept(ruleSet);

                if (onAlternatingRulesetRemoved != null && !isSelected)
                    onAlternatingRulesetRemoved.accept(ruleSet);

                ruleSetMenu.setText("ALTERNATING");
            });


            alternatingSelectionMenu.add(ruleSetItem);
        }


        alternatingSelectionMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (onRuleSetSelected != null)
                    onRuleSetSelected.accept(RuleSet.ALTERNATING);
            }

            @Override
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });

        ruleSetMenu.add(alternatingSelectionMenu);


        add(ruleSetMenu);
    }

    private void addEditMenu() {
        JMenu editMenu = new JMenu("EDIT");

        JMenuItem randomizeItem = new JMenuItem("Randomize Grid");
        randomizeItem.addActionListener(e -> {
            if (onRandomizeRequested != null)
                onRandomizeRequested.run();
        });
        editMenu.add(randomizeItem);

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

    public void setOnAlternatingRulesetAdded(Consumer<RuleSet> onAlternatingRulesetAdded) {
        this.onAlternatingRulesetAdded = onAlternatingRulesetAdded;
    }

    public void setOnAlternatingRulesetRemoved(Consumer<RuleSet> onAlternatingRulesetRemoved) {
        this.onAlternatingRulesetRemoved = onAlternatingRulesetRemoved;
    }

    public void setOnRandomizeRequested(Runnable onRandomizeRequested) {
        this.onRandomizeRequested = onRandomizeRequested;
    }
}
