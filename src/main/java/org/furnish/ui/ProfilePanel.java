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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ProfilePanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable userTable;
    private List<User> users;
    private JButton addButton, editButton, deleteButton;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initializeComponents();
        loadUsers();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columns = {"Username", "Email", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setBackground(new Color(240, 240, 245));
        userTable.setForeground(new Color(60, 60, 60));
        userTable.setFont(new Font("Montserrat", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 12));
        userTable.setRowHeight(30);

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        addButton = createButton("Add User", new Color(92, 184, 92));
        editButton = createButton("Edit", new Color(240, 173, 78));
        deleteButton = createButton("Delete", new Color(217, 83, 79));

        // Add action listeners
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                showAddEditDialog(users.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this user?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteUser(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.",
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

    private void loadUsers() {
        users = new ArrayList<>();
        // Add some sample users
        users.add(new User("admin", "admin@furnish.com", "Administrator", "Active"));
        users.add(new User("designer", "designer@furnish.com", "Designer", "Active"));
        users.add(new User("user", "user@furnish.com", "User", "Inactive"));
        
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getStatus()
            });
        }
    }

    private void showAddEditDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                user == null ? "Add User" : "Edit User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        if (user != null) usernameField.setText(user.getUsername());
        formPanel.add(usernameField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        if (user != null) emailField.setText(user.getEmail());
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Role field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Administrator", "Designer", "User"});
        if (user != null) roleCombo.setSelectedItem(user.getRole());
        formPanel.add(roleCombo, gbc);

        // Status field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        if (user != null) statusCombo.setSelectedItem(user.getStatus());
        formPanel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton saveButton = createButton("Save", new Color(92, 184, 92));
        JButton cancelButton = createButton("Cancel", new Color(217, 83, 79));

        saveButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(
                    usernameField.getText(),
                    emailField.getText(),
                    (String) roleCombo.getSelectedItem(),
                    (String) statusCombo.getSelectedItem()
            );

            if (user == null) {
                users.add(newUser);
            } else {
                int index = users.indexOf(user);
                users.set(index, newUser);
            }

            refreshTable();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteUser(int index) {
        users.remove(index);
        refreshTable();
    }

    // Inner class to represent users
    private static class User {
        private String username;
        private String email;
        private String role;
        private String status;

        public User(String username, String email, String role, String status) {
            this.username = username;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
    }
} 