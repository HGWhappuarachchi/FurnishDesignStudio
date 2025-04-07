package org.furnish;

import javax.swing.*;

import org.furnish.utils.CloseButtonUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class OnboardingScreen extends JFrame {
    public OnboardingScreen() {
        setTitle("Furnish Studio");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 650, 30, 30));

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
                for (int i = 0; i < 5; i++) {
                    g2d.fillOval(150 + i * 120, 80, 80, 80);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        JButton closeButton = CloseButtonUtil.createCloseButton();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(closeButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // App logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("./images/sofa.png"));
        JLabel logo = new JLabel(logoIcon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome message
        JLabel welcomeTitle = new JLabel("FURNISH STUDIO");
        welcomeTitle.setFont(new Font("Montserrat", Font.BOLD, 40));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeTitle.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("Design Your Dream Space");
        welcomeLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        // Subtitle
        JLabel subtitle = new JLabel("Visualize furniture in your room before buying");
        subtitle.setFont(new Font("Montserrat", Font.PLAIN, 16));
        subtitle.setForeground(new Color(200, 200, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        // Get Started button
        JButton getStartedButton = new RoundedButton("GET STARTED");
        getStartedButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        getStartedButton.setBackground(new Color(92, 184, 92));
        getStartedButton.setForeground(Color.WHITE);
        getStartedButton.setFocusPainted(false);
        getStartedButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        getStartedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStartedButton.addActionListener(e -> {
            Timer timer = new Timer(10, new ActionListener() {
                float opacity = 1f;

                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity -= 0.05f;
                    if (opacity <= 0) {
                        ((Timer) e.getSource()).stop();
                        new LoginScreen().setVisible(true);
                        dispose();
                    }
                    setOpacity(opacity);
                }
            });
            timer.start();
        });

        contentPanel.add(logo);
        contentPanel.add(welcomeTitle);
        contentPanel.add(welcomeLabel);
        contentPanel.add(subtitle);
        contentPanel.add(getStartedButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Furnish Studio | Terms | Privacy");
        footer.setFont(new Font("Montserrat", Font.PLAIN, 12));
        footer.setForeground(new Color(150, 150, 150));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OnboardingScreen screen = new OnboardingScreen();
            screen.setOpacity(0f);
            screen.setVisible(true);

            Timer timer = new Timer(10, new ActionListener() {
                float opacity = 0f;

                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity += 0.05f;
                    if (opacity >= 1f) {
                        ((Timer) e.getSource()).stop();
                    }
                    screen.setOpacity(opacity);
                }
            });
            timer.start();
        });
    }
}