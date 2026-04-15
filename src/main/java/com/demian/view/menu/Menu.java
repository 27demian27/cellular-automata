package com.demian.view.menu;

import com.demian.model.Plane;
import com.demian.model.RuleSet;
import com.demian.view.Grid;
import com.demian.view.painting.PaintMode;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Menu extends JMenuBar {

    @Setter
    private Runnable onClearRequested;
    @Setter
    private Runnable onNextGenerationRequested;
    @Setter
    private Runnable onRandomizeRequested;
    @Setter
    private Runnable onStateSaveRequested;


    @Setter
    private Consumer<RuleSet> onAlternatingRulesetAdded;
    @Setter
    private Consumer<RuleSet> onAlternatingRulesetRemoved;
    @Setter
    private Consumer<RuleSet> onRuleSetSelected;
    @Setter
    private BiConsumer<List<Integer>, List<Integer>> onCustomRulesetChanged;
    @Setter
    private Consumer<String> onStateLoadRequested;

    private final Frame frame;
    private final Plane plane;
    private final Grid grid;

    public Menu(Frame frame, Plane plane, Grid grid) {
        this.frame = frame;
        this.plane = plane;
        this.grid = grid;

        addRuleSetMenu();
        addEditMenu();
        addGridMenu();
        addBrushMenu();
        addStateMenu();
    }

    public void showErrorDialog(Exception e) {
        e.printStackTrace();
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

        JMenuItem undoItem = new JMenuItem("Undo Paint");
        undoItem.addActionListener(e -> grid.undoRecentPaint());
        editMenu.add(undoItem);

        JMenuItem nextGenItem = new JMenuItem("Next Generation");
        nextGenItem.addActionListener(e -> {
            if (onNextGenerationRequested != null)
                onNextGenerationRequested.run();
        });
        editMenu.add(nextGenItem);

        JMenuItem editCustomRulesetItem = getEditCustomRulesetItem();
        editMenu.add(editCustomRulesetItem);


        add(editMenu);
    }

    private void addGridMenu() {
        JMenu gridMenu = new JMenu("GRID");

        JMenuItem randomizeItem = new JMenuItem("Randomize Grid");
        randomizeItem.addActionListener(e -> {
            if (onRandomizeRequested != null)
                onRandomizeRequested.run();
        });
        gridMenu.add(randomizeItem);

        JMenuItem clearItem = new JMenuItem("Clear Grid");
        clearItem.addActionListener(e -> {
            if (onClearRequested != null)
                onClearRequested.run();
        });
        gridMenu.add(clearItem);

        JMenuItem toggleGridLinesItem = new JMenuItem("Show Grid Lines");
        toggleGridLinesItem.addActionListener(e -> grid.toggleGridLines());
        gridMenu.add(toggleGridLinesItem);

        JMenuItem showDebugItem = new JMenuItem("Show Debug");
        showDebugItem.addActionListener(e -> grid.toggleShowDebug());
        gridMenu.add(showDebugItem);

        JMenuItem resizeGridItem = getResizeGridItem();
        gridMenu.add(resizeGridItem);

        add(gridMenu);
    }

    private JMenuItem getEditCustomRulesetItem() {
        JMenuItem editCustomRulesetItem = new JMenuItem("Edit Custom Ruleset");
        editCustomRulesetItem.addActionListener(e -> {
            CustomRuleDialog customRuleDialog = new CustomRuleDialog(frame, plane);
            customRuleDialog.setVisible(true);

            if (customRuleDialog.isConfirmed()) {
                List<Integer> birthRuleset = customRuleDialog.getSelectedBirth();
                List<Integer> surviveRuleset = customRuleDialog.getSelectedSurvive();

                onCustomRulesetChanged.accept(birthRuleset, surviveRuleset);
            }
        });
        return editCustomRulesetItem;
    }

    private JMenuItem getResizeGridItem() {
        JMenuItem resizeGridItem = new JMenuItem("Resize Grid");
        resizeGridItem.addActionListener(e -> {
            ResizeDialog dialog = new ResizeDialog(frame, plane);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                int x1 = dialog.getX1();
                int x2 = dialog.getX2();
                int y1 = dialog.getY1();
                int y2 = dialog.getY2();

                // Moet via controller
                plane.resize(x1, x2, y1, y2);
                grid.translate(x1, y1);
                grid.repaint();
            }
        });
        return resizeGridItem;
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

    private void addStateMenu() {
        JMenu stateMenu = new JMenu("SAVE/LOAD");

        JMenuItem saveItem = new JMenuItem("Save State");
        saveItem.addActionListener(e -> {
            if (onStateSaveRequested != null)
                onStateSaveRequested.run();
        });
        stateMenu.add(saveItem);

        JMenuItem loadItem = new JMenuItem("Load State");
        loadItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("src/main/resources/saves"));
            chooser.setFileFilter(new FileNameExtensionFilter("Save Files", "stt"));

            int result = chooser.showOpenDialog(frame);;

            if (result == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getName();

                if (onStateLoadRequested != null)
                    onStateLoadRequested.accept(fileName);
            }
            grid.repaint();
        });
        stateMenu.add(loadItem);


        add(stateMenu);
    }

}
