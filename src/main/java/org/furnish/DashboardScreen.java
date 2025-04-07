package org.furnish;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.furnish.ui.RoomDesignerPanel;
import org.furnish.utils.CloseButtonUtil;

public class DashboardScreen extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public DashboardScreen() {
        setTitle("Furnish Studio - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1200, 800, 30, 30));

        // Main panel with gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(23, 23, 38), 0, getHeight(),
                        new Color(42, 42, 74));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Top panel with close button and user info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton closeButton = CloseButtonUtil.createCloseButton();
        topPanel.add(closeButton, BorderLayout.EAST);

        JLabel userLabel = new JLabel("Welcome, User");
        userLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        topPanel.add(userLabel, BorderLayout.WEST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Sidebar with navigation
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(250, 0));

        String[] menuItems = {"Room Designer", "Furniture Catalog", "My Designs", "Profile"};
        for (String item : menuItems) {
            JButton menuButton = new RoundedButton(item);
            menuButton.setFont(new Font("Montserrat", Font.PLAIN, 14));
            menuButton.setBackground(new Color(92, 184, 92));
            menuButton.setForeground(Color.WHITE);
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(200, 40));
            menuButton.addActionListener(e -> switchPanel(item));
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Add different panels
        contentPanel.add(createRoomDesignerPanel(), "Room Designer");
        contentPanel.add(createFurnitureCatalogPanel(), "Furniture Catalog");
        contentPanel.add(createMyDesignsPanel(), "My Designs");
        contentPanel.add(createProfilePanel(), "Profile");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void switchPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    private JPanel createRoomDesignerPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Room Designer", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        // Add room designer panel
        RoomDesignerPanel designerPanel = new RoomDesignerPanel();
        panel.add(designerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createFurnitureCatalogPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Furniture Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        // Add furniture catalog components here
        JPanel catalogPanel = new JPanel();
        catalogPanel.setOpaque(false);
        panel.add(catalogPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createMyDesignsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("My Designs", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        // Add saved designs components here
        JPanel designsPanel = new JPanel();
        designsPanel.setOpaque(false);
        panel.add(designsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Profile", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        
        // Add profile components here
        JPanel profilePanel = new JPanel();
        profilePanel.setOpaque(false);
        panel.add(profilePanel, BorderLayout.CENTER);
        
        return panel;
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