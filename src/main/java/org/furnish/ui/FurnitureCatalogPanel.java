package org.furnish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class FurnitureCatalogPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(60, 60, 90);
    private static final Color TABLE_BACKGROUND = new Color(70, 70, 100);
    private static final Color TABLE_HEADER_COLOR = new Color(50, 50, 80);
    private static final Color TABLE_SELECTION_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_COLOR = new Color(236, 240, 241);
    private static final Color BUTTON_ADD_COLOR = new Color(46, 204, 113);
    private static final Color BUTTON_EDIT_COLOR = new Color(241, 196, 15);
    private static final Color BUTTON_DELETE_COLOR = new Color(231, 76, 60);
    private static final Color BUTTON_HOVER = new Color(52, 73, 94);

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

        // Create table with custom styling
        furnitureTable = new JTable(tableModel) {
            @Override
            public JTableHeader getTableHeader() {
                JTableHeader header = super.getTableHeader();
                header.setDefaultRenderer(new HeaderRenderer());
                return header;
            }
        };
        
        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        furnitureTable.getTableHeader().setReorderingAllowed(false);
        furnitureTable.setBackground(TABLE_BACKGROUND);
        furnitureTable.setForeground(TEXT_COLOR);
        furnitureTable.setFont(new Font("Montserrat", Font.PLAIN, 12));
        furnitureTable.setRowHeight(40);
        furnitureTable.setShowGrid(false);
        furnitureTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        furnitureTable.setSelectionBackground(TABLE_SELECTION_COLOR);
        furnitureTable.setSelectionForeground(TEXT_COLOR);
        
        // Set column widths
        TableColumnModel columnModel = furnitureTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Name
        columnModel.getColumn(1).setPreferredWidth(100); // Type
        columnModel.getColumn(2).setPreferredWidth(120); // Dimensions
        columnModel.getColumn(3).setPreferredWidth(100); // Price
        columnModel.getColumn(4).setPreferredWidth(100); // Color

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(furnitureTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Create buttons with custom styling
        addButton = createStyledButton("Add Furniture", BUTTON_ADD_COLOR);
        editButton = createStyledButton("Edit", BUTTON_EDIT_COLOR);
        deleteButton = createStyledButton("Delete", BUTTON_DELETE_COLOR);

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

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Montserrat", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        return button;
    }

    private class HeaderRenderer implements TableCellRenderer {
        private JLabel label;
        
        public HeaderRenderer() {
            label = new JLabel();
            label.setOpaque(true);
            label.setBackground(TABLE_HEADER_COLOR);
            label.setForeground(TEXT_COLOR);
            label.setFont(new Font("Montserrat", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            label.setText(value.toString());
            return label;
        }
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
        JButton saveButton = createStyledButton("Save", BUTTON_ADD_COLOR);
        JButton cancelButton = createStyledButton("Cancel", BUTTON_DELETE_COLOR);

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