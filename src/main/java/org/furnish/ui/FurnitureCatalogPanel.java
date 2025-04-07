package org.furnish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FurnitureCatalogPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable furnitureTable;
    private List<FurnitureItem> furnitureItems;
    private JButton addButton, editButton, deleteButton;

    public FurnitureCatalogPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initializeComponents();
        loadFurnitureItems();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columns = {"Name", "Type", "Dimensions", "Price", "Color"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        furnitureTable = new JTable(tableModel);
        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        furnitureTable.getTableHeader().setReorderingAllowed(false);
        furnitureTable.setBackground(new Color(240, 240, 245));
        furnitureTable.setForeground(new Color(60, 60, 60));
        furnitureTable.setFont(new Font("Montserrat", Font.PLAIN, 12));
        furnitureTable.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 12));
        furnitureTable.setRowHeight(30);

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(furnitureTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        addButton = createButton("Add Furniture", new Color(92, 184, 92));
        editButton = createButton("Edit", new Color(240, 173, 78));
        deleteButton = createButton("Delete", new Color(217, 83, 79));

        // Add action listeners
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = furnitureTable.getSelectedRow();
            if (selectedRow >= 0) {
                showAddEditDialog(furnitureItems.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = furnitureTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this furniture item?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteFurnitureItem(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a furniture item to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void loadFurnitureItems() {
        furnitureItems = new ArrayList<>();
        // Add some sample furniture items
        furnitureItems.add(new FurnitureItem("Modern Sofa", "Sofa", "200x80x70", 999.99, "Gray"));
        furnitureItems.add(new FurnitureItem("Dining Table", "Table", "180x90x75", 499.99, "Brown"));
        furnitureItems.add(new FurnitureItem("Office Chair", "Chair", "50x50x100", 199.99, "Black"));
        
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (FurnitureItem item : furnitureItems) {
            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getType(),
                    item.getDimensions(),
                    String.format("LKR %.2f", item.getPrice()),
                    item.getColor()
            });
        }
    }

    private void showAddEditDialog(FurnitureItem item) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                item == null ? "Add Furniture" : "Edit Furniture", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        if (item != null) nameField.setText(item.getName());
        formPanel.add(nameField, gbc);

        // Type field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Sofa", "Table", "Chair", "Cabinet"});
        if (item != null) typeCombo.setSelectedItem(item.getType());
        formPanel.add(typeCombo, gbc);

        // Dimensions field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Dimensions:"), gbc);
        gbc.gridx = 1;
        JTextField dimensionsField = new JTextField(20);
        if (item != null) dimensionsField.setText(item.getDimensions());
        formPanel.add(dimensionsField, gbc);

        // Price field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField(20);
        if (item != null) priceField.setText(String.valueOf(item.getPrice()));
        formPanel.add(priceField, gbc);

        // Color field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Color:"), gbc);
        gbc.gridx = 1;
        JTextField colorField = new JTextField(20);
        if (item != null) colorField.setText(item.getColor());
        formPanel.add(colorField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton saveButton = createButton("Save", new Color(92, 184, 92));
        JButton cancelButton = createButton("Cancel", new Color(217, 83, 79));

        saveButton.addActionListener(e -> {
            try {
                FurnitureItem newItem = new FurnitureItem(
                        nameField.getText(),
                        (String) typeCombo.getSelectedItem(),
                        dimensionsField.getText(),
                        Double.parseDouble(priceField.getText()),
                        colorField.getText()
                );

                if (item == null) {
                    furnitureItems.add(newItem);
                } else {
                    int index = furnitureItems.indexOf(item);
                    furnitureItems.set(index, newItem);
                }

                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteFurnitureItem(int index) {
        furnitureItems.remove(index);
        refreshTable();
    }

    // Inner class to represent furniture items
    private static class FurnitureItem {
        private String name;
        private String type;
        private String dimensions;
        private double price;
        private String color;

        public FurnitureItem(String name, String type, String dimensions, double price, String color) {
            this.name = name;
            this.type = type;
            this.dimensions = dimensions;
            this.price = price;
            this.color = color;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getDimensions() { return dimensions; }
        public double getPrice() { return price; }
        public String getColor() { return color; }
    }
} 