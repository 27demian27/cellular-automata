package com.demian.view;

import com.demian.model.RuleSet;
import com.demian.view.painting.PaintMode;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.function.Consumer;

public class Menu extends JMenuBar {

    private Runnable onClearRequested;
    private Runnable onNextGenerationRequested;
    private Runnable onRandomizeRequested;


    private Consumer<RuleSet> onAlternatingRulesetAdded;
    private Consumer<RuleSet> onAlternatingRulesetRemoved;
    private Consumer<RuleSet> onRuleSetSelected;
    private Consumer<PaintMode> onPaintModeChanged;

    private final Grid grid;

    public Menu(Grid grid) {
        this.grid = grid;

        addRuleSetMenu();
        addEditMenu();
        addBrushMenu();
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

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> grid.undoRecentPaint());
        editMenu.add(undoItem);

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

        JMenuItem toggleGridLinesItem = new JMenuItem("Toggle Grid Lines");
        toggleGridLinesItem.addActionListener(e -> grid.toggleGridLines());
        editMenu.add(toggleGridLinesItem);

        add(editMenu);
    }

    private void addBrushMenu() {
        JMenu brushMenu = new JMenu("SELECT BRUSH");

        JMenuItem paintBrushItem = new JMenuItem("PAINTING");
        paintBrushItem.addActionListener(e -> {
            grid.setPaintMode(PaintMode.NORMAL);
            brushMenu.setText("PAINTING");
        });
        brushMenu.add(paintBrushItem);

        JMenuItem eraseBrushItem = new JMenuItem("ERASING");
        eraseBrushItem.addActionListener(e -> {
            grid.setPaintMode(PaintMode.ERASE);
            brushMenu.setText("ERASING");
        });
        brushMenu.add(eraseBrushItem);

        add(brushMenu);
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
