package org.furnish;

import javax.swing.*;

import org.furnish.ui.FurnitureDesignApp;
import org.furnish.utils.CloseButtonUtil;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Furnish Studio - Login");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 30, 30));

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(23, 23, 38), 0, getHeight(),
                        new Color(42, 42, 74));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < 3; i++) {
                    g2d.fillOval(100 + i * 150, 50, 80, 80);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Close button
        JButton closeButton = CloseButtonUtil.createCloseButton();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        topPanel.add(closeButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("./images/sofa.png"));
        JLabel logo = new JLabel(logoIcon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("WELCOME BACK");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Input fields panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Username field
        JTextField usernameField = createStyledTextField("Username");
        inputPanel.add(usernameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password field
        JPasswordField passwordField = createStyledPasswordField("Password");
        inputPanel.add(passwordField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login button
        JButton loginButton = new RoundedButton("LOGIN");
        loginButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        loginButton.setBackground(new Color(92, 184, 92));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) {
                new FurnitureDesignApp().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sign up prompt
        JLabel signUpLabel = new JLabel("Don't have an account?");
        signUpLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        signUpLabel.setForeground(new Color(200, 200, 200));
        signUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sign up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        signUpButton.setContentAreaFilled(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setForeground(new Color(92, 184, 92));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(e -> {
            new SignUpScreen().setVisible(true);
            dispose();
        });

        // Add components to content panel
        contentPanel.add(logo);
        contentPanel.add(titleLabel);
        contentPanel.add(inputPanel);
        contentPanel.add(loginButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(signUpLabel);
        contentPanel.add(signUpButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Furnish Studio | Terms | Privacy");
        footer.setFont(new Font("Montserrat", Font.PLAIN, 12));
        footer.setForeground(new Color(150, 150, 150));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    // Custom styled text field
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Montserrat", Font.PLAIN, 16));
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(60, 60, 90));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        textField.setCaretColor(Color.WHITE);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Placeholder text
        textField.setText(placeholder);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });

        return textField;
    }

    // Custom styled password field
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Montserrat", Font.PLAIN, 16));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(60, 60, 90));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(placeholder);
                }
            }
        });

        return passwordField;
    }

    private boolean authenticate(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }

    // Custom rounded button class
    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
        }

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

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}