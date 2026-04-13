package com.demian.view.menu;

import com.demian.model.Plane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomRuleDialog extends JDialog {

    private final Plane plane;

    private final List<JToggleButton> buttonsBirth = new ArrayList<>();
    private final List<JToggleButton> buttonsSurvive = new ArrayList<>();

    private boolean confirmed = false;

    public CustomRuleDialog(Window parent, Plane plane) {
        super(parent, "Select Numbers", ModalityType.APPLICATION_MODAL);
        this.plane = plane;
        buildUI();
    }

    private void buildUI() {

        JPanel grid = new JPanel(new GridLayout(2, 10, 8, 8));

        grid.add(new JLabel("Birth"));
        for (int i = 0; i <= 8; i++) {
            JToggleButton btn = new JToggleButton(String.valueOf(i));
            if (plane.getCustomRuleBirth().contains(i))
                btn.setSelected(true);
            buttonsBirth.add(btn);
            grid.add(btn);
        }

        grid.add(new JLabel("Survive"));
        for (int i = 0; i <= 8; i++) {
            JToggleButton btn = new JToggleButton(String.valueOf(i));
            if (plane.getCustomRuleSurvival().contains(i))
                btn.setSelected(true);
            buttonsSurvive.add(btn);
            grid.add(btn);
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::onOK);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(grid, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onOK(ActionEvent e) {
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Integer> getSelectedBirth() {

        return buttonsBirth.stream()
                .filter(JToggleButton::isSelected)
                .map(e -> Integer.parseInt(e.getText()))
                .toList();

    }

    public List<Integer> getSelectedSurvive() {
        return buttonsSurvive.stream()
                .filter(JToggleButton::isSelected)
                .map(e -> Integer.parseInt(e.getText()))
                .toList();

    }
}