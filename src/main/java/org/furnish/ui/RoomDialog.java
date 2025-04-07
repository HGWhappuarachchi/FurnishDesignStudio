package org.furnish.ui;

import org.furnish.core.Room;
import javax.swing.*;
import java.awt.*;

public class RoomDialog extends JDialog {
    private JTextField lengthField, widthField, heightField;
    private JButton floorColorButton, wallColorButton;
    private Color floorColor = Color.LIGHT_GRAY;
    private Color wallColor = Color.WHITE;
    private boolean ok = false;

    public RoomDialog(JFrame parent) {
        super(parent, "New Room", true);
        initializeDialog();
        setupInputFields();
    }

    private void initializeDialog() {
        setLayout(new GridLayout(0, 2));
        setSize(300, 200);
    }

    private void setupInputFields() {
        // Length
        add(new JLabel("Length:"));
        lengthField = new JTextField("5.0", 5);
        add(lengthField);

        // Width
        add(new JLabel("Width:"));
        widthField = new JTextField("5.0", 5);
        add(widthField);

        // Height
        add(new JLabel("Height:"));
        heightField = new JTextField("3.0", 5);
        add(heightField);

        // Floor Color
        add(new JLabel("Floor Color:"));
        floorColorButton = new JButton("Choose");
        floorColorButton.addActionListener(e -> {
            Color c = JColorChooser.showDialog(
                    this, "Choose Floor Color", floorColor);
            if (c != null)
                floorColor = c;
        });
        add(floorColorButton);

        // Wall Color
        add(new JLabel("Wall Color:"));
        wallColorButton = new JButton("Choose");
        wallColorButton.addActionListener(e -> {
            Color c = JColorChooser.showDialog(
                    this, "Choose Wall Color", wallColor);
            if (c != null)
                wallColor = c;
        });
        add(wallColorButton);

        // Buttons
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            ok = true;
            setVisible(false);
        });
        add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);
    }

    public boolean isOk() {
        return ok;
    }

    public Room getRoom() {
        try {
            double length = Double.parseDouble(lengthField.getText());
            double width = Double.parseDouble(widthField.getText());
            double height = Double.parseDouble(heightField.getText());
            return new Room(length, width, height, floorColor, wallColor);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid room dimensions");
        }
    }
}