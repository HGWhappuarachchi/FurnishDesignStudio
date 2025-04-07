package org.furnish;

import javax.swing.*;
import org.furnish.utils.CloseButtonUtil;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SignUpScreen extends JFrame {
    public SignUpScreen() {
        setTitle("Furnish Studio - Sign Up");
        setSize(500, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 850, 30, 30));

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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("./images/sofa.png"));
        JLabel logo = new JLabel(logoIcon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Input fields panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Full Name field
        JTextField nameField = createStyledTextField("Full Name");
        inputPanel.add(nameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email field
        JTextField emailField = createStyledTextField("Email");
        inputPanel.add(emailField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Username field
        JTextField usernameField = createStyledTextField("Username");
        inputPanel.add(usernameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password field
        JPasswordField passwordField = createStyledPasswordField("Password");
        inputPanel.add(passwordField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm Password field
        JPasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");
        inputPanel.add(confirmPasswordField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Sign up button
        JButton signUpButton = new RoundedButton("CREATE ACCOUNT");
        signUpButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        signUpButton.setBackground(new Color(92, 184, 92));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save user data
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new LoginScreen().setVisible(true);
            dispose();
        });

        // Login prompt
        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        loginLabel.setForeground(new Color(200, 200, 200));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setForeground(new Color(92, 184, 92));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        // Add components to content panel
        contentPanel.add(logo);
        contentPanel.add(titleLabel);
        contentPanel.add(inputPanel);
        contentPanel.add(signUpButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(loginLabel);
        contentPanel.add(loginButton);

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