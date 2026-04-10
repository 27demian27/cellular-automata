package com.demian.view.menu;

import com.demian.model.Plane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResizeDialog extends JDialog {

    private final JTextField x1Field = new JTextField(5);
    private final JTextField x2Field = new JTextField(5);
    private final JTextField y1Field = new JTextField(5);
    private final JTextField y2Field = new JTextField(5);

    private boolean confirmed = false;

    private final Plane plane;

    public ResizeDialog(Window parent, Plane plane) {
        super(parent, "Resize", ModalityType.APPLICATION_MODAL);
        this.plane = plane;
        buildUI();
    }

    private void buildUI() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));

        inputPanel.add(new JLabel("x1:"));
        x1Field.setText(String.valueOf(0));
        inputPanel.add(x1Field);

        inputPanel.add(new JLabel("x2:"));
        x2Field.setText(String.valueOf(plane.getSizeX()));
        inputPanel.add(x2Field);

        inputPanel.add(new JLabel("y1:"));
        y1Field.setText(String.valueOf(0));
        inputPanel.add(y1Field);

        inputPanel.add(new JLabel("y2:"));
        y2Field.setText(String.valueOf(plane.getSizeY()));
        inputPanel.add(y2Field);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this::onOK);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onOK(ActionEvent e) {
        if (validateInput()) {
            confirmed = true;
            dispose();
        }
    }

    private boolean validateInput() {
        try {
            Integer.parseInt(x1Field.getText());
            Integer.parseInt(x2Field.getText());
            Integer.parseInt(y1Field.getText());
            Integer.parseInt(y2Field.getText());
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers.");
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getX1() { return Integer.parseInt(x1Field.getText()); }
    public int getX2() { return Integer.parseInt(x2Field.getText()); }
    public int getY1() { return Integer.parseInt(y1Field.getText()); }
    public int getY2() { return Integer.parseInt(y2Field.getText()); }
}