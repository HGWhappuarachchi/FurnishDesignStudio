package org.furnish.ui;

import org.furnish.core.Furniture;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Hashtable;

public class PropertiesPanel extends JPanel {
    private JTextField xField, zField, widthField, depthField, heightField;
    private JButton colorButton;
    private JSlider shadeSlider;
    private Furniture furniture;

    // Colors and fonts
    private final Color BACKGROUND_COLOR = new Color(23, 23, 38);
    private final Color FIELD_BACKGROUND = new Color(60, 60, 90);
    private final Color BORDER_COLOR = new Color(80, 80, 110);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color HIGHLIGHT_COLOR = new Color(92, 184, 92);
    private final Font LABEL_FONT = new Font("Montserrat", Font.BOLD, 14);
    private final Font FIELD_FONT = new Font("Montserrat", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Montserrat", Font.BOLD, 18);
    private static final int VERTICAL_GAP = 20;

    public PropertiesPanel() {
        initializePanel();
        setupPropertyFields();
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(300, 600));
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void setupPropertyFields() {
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("FURNITURE PROPERTIES");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(45, 5, 15, 5));
        add(titlePanel, BorderLayout.NORTH);

        // Fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(35, 5, 15, 5));

        // Input fields
        fieldsPanel.add(createPropertyField("X Position", xField = createStyledTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        fieldsPanel.add(createPropertyField("Z Position", zField = createStyledTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        fieldsPanel.add(createPropertyField("Width", widthField = createStyledTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        fieldsPanel.add(createPropertyField("Depth", depthField = createStyledTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        fieldsPanel.add(createPropertyField("Height", heightField = createStyledTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        // Color button
        colorButton = createStyledButton("Choose Color");
        colorButton.addActionListener(e -> {
            if (furniture != null) {
                Color newColor = JColorChooser.showDialog(
                        this, "Choose Color", furniture.getColor());
                if (newColor != null) {
                    furniture.setColor(newColor);
                    colorButton.setBackground(newColor);
                    repaintParent();
                }
            }
        });
        fieldsPanel.add(createPropertyField("Color", colorButton));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP)));

        // Shade slider
        shadeSlider = createStyledSlider(0, 100, 100);
        fieldsPanel.add(createPropertyField("Shade", shadeSlider));

        // Add fields panel to main panel
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Update button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_GAP, 0, 20, 0));

        JButton updateButton = createStyledButton("Update Properties");
        updateButton.setBackground(HIGHLIGHT_COLOR);
        updateButton.addActionListener(e -> {
            if (furniture != null) {
                updateAllProperties();
            }
        });

        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add listeners
        setupFieldListeners();
    }

    private JPanel createPropertyField(String labelText, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        label.setMinimumSize(new Dimension(100, label.getPreferredSize().height));
        label.setMaximumSize(new Dimension(100, label.getPreferredSize().height));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(component);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(10);
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_BACKGROUND);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));

        // Add hover effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                updatePropertyFromField(field);
            }
        });

        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(FIELD_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(FIELD_BACKGROUND);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
        });

        return button;
    }

    private JSlider createStyledSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setOpaque(false);
        slider.setForeground(TEXT_COLOR);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(25);

        // Style the labels
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 100; i += 25) {
            JLabel label = new JLabel(String.valueOf(i));
            label.setFont(new Font("Montserrat", Font.PLAIN, 10));
            label.setForeground(TEXT_COLOR);
            labelTable.put(i, label);
        }
        slider.setLabelTable(labelTable);

        // Custom UI for slider
        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HIGHLIGHT_COLOR);
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background track
                g2d.setColor(FIELD_BACKGROUND);
                g2d.fillRoundRect(trackRect.x, trackRect.y + 5, trackRect.width, 5, 3, 3);

                // Filled track
                int thumbPos = thumbRect.x + thumbRect.width / 2;
                g2d.setColor(HIGHLIGHT_COLOR);
                g2d.fillRoundRect(trackRect.x, trackRect.y + 5, thumbPos - trackRect.x, 5, 3, 3);
            }
        });

        // Add change listener
        slider.addChangeListener(e -> {
            if (furniture != null) {
                furniture.setShadeFactor(slider.getValue() / 100f);
                repaintParent();
            }
        });

        return slider;
    }

    private void setupFieldListeners() {

        // Enter key listener
        ActionListener enterAction = e -> {
            JTextField source = (JTextField) e.getSource();
            updatePropertyFromField(source);
        };

        xField.addActionListener(enterAction);
        zField.addActionListener(enterAction);
        widthField.addActionListener(enterAction);
        depthField.addActionListener(enterAction);
        heightField.addActionListener(enterAction);
    }

    private void updatePropertyFromField(JTextField field) {
        if (furniture == null)
            return;

        try {
            double value = Double.parseDouble(field.getText());

            if (field == xField)
                furniture.setX(value);
            else if (field == zField)
                furniture.setZ(value);
            else if (field == widthField)
                furniture.setWidth(value);
            else if (field == depthField)
                furniture.setDepth(value);
            else if (field == heightField)
                furniture.setHeight(value);

            repaintParent();
        } catch (NumberFormatException ex) {
            // Reset to current value
            showError("Please enter a valid number");
            updateField(field);
        }
    }

    private void updateField(JTextField field) {
        if (furniture == null)
            return;

        if (field == xField)
            field.setText(String.format("%.1f", furniture.getX()));
        else if (field == zField)
            field.setText(String.format("%.1f", furniture.getZ()));
        else if (field == widthField)
            field.setText(String.format("%.1f", furniture.getWidth()));
        else if (field == depthField)
            field.setText(String.format("%.1f", furniture.getDepth()));
        else if (field == heightField)
            field.setText(String.format("%.1f", furniture.getHeight()));
    }

    private void updateAllProperties() {
        try {
            double x = Double.parseDouble(xField.getText());
            double z = Double.parseDouble(zField.getText());
            double width = Double.parseDouble(widthField.getText());
            double depth = Double.parseDouble(depthField.getText());
            double height = Double.parseDouble(heightField.getText());

            furniture.setX(x);
            furniture.setZ(z);
            furniture.setWidth(width);
            furniture.setDepth(depth);
            furniture.setHeight(height);

            repaintParent();
        } catch (NumberFormatException ex) {
            showError("Invalid values detected. Please check your inputs.");
            update(furniture);
        }
    }

    private void showError(String message) {
        JDialog errorDialog = new JDialog();
        errorDialog.setUndecorated(true);
        errorDialog.setShape(new RoundRectangle2D.Double(0, 0, 300, 120, 20, 20));

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(BACKGROUND_COLOR);
        errorPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel errorIcon = new JLabel("⚠️");
        errorIcon.setFont(new Font("Dialog", Font.PLAIN, 24));
        errorIcon.setForeground(Color.ORANGE);
        errorIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel errorMessage = new JLabel(message);
        errorMessage.setFont(FIELD_FONT);
        errorMessage.setForeground(TEXT_COLOR);
        errorMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JButton okButton = createStyledButton("OK");
        okButton.setBackground(HIGHLIGHT_COLOR);
        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        errorPanel.add(errorIcon, BorderLayout.NORTH);
        errorPanel.add(errorMessage, BorderLayout.CENTER);
        errorPanel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.add(errorPanel);
        errorDialog.pack();
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
    }

    public void update(Furniture f) {
        this.furniture = f;
        if (f == null) {
            clearFields();
        } else {
            populateFields(f);
            colorButton.setBackground(f.getColor());
        }
    }

    private void clearFields() {
        xField.setText("");
        zField.setText("");
        widthField.setText("");
        depthField.setText("");
        heightField.setText("");
        shadeSlider.setValue(100);
        colorButton.setBackground(FIELD_BACKGROUND);
    }

    private void populateFields(Furniture f) {
        xField.setText(String.format("%.1f", f.getX()));
        zField.setText(String.format("%.1f", f.getZ()));
        widthField.setText(String.format("%.1f", f.getWidth()));
        depthField.setText(String.format("%.1f", f.getDepth()));
        heightField.setText(String.format("%.1f", f.getHeight()));
        shadeSlider.setValue((int) (f.getShadeFactor() * 100));
    }

    private void repaintParent() {
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background
        GradientPaint gradient = new GradientPaint(
                0, 0,
                new Color(23, 23, 38),
                0, getHeight(),
                new Color(42, 42, 74));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Decorative elements
        g2d.setColor(new Color(255, 255, 255, 10));
        g2d.fillOval(-50, -50, 150, 150);
        g2d.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
    }
}