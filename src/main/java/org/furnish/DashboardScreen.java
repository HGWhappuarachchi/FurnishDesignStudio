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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.furnish.ui.FurnitureCatalogPanel;
import org.furnish.ui.MyDesignsPanel;
import org.furnish.ui.ProfilePanel;
import org.furnish.ui.RoomDesignerPanel;
import org.furnish.utils.CloseButtonUtil;

public class DashboardScreen extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(23, 23, 38);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color TEXT_COLOR = new Color(236, 240, 241);
    private static final Color SIDEBAR_COLOR = new Color(30, 30, 50);
    private static final Color MENU_HOVER = new Color(52, 73, 94);
    private static final Color MENU_SELECTED = new Color(41, 128, 185);

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedMenuButton;

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
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(),
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
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JButton closeButton = CloseButtonUtil.createCloseButton();
        closeButton.setBackground(new Color(255, 255, 255, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(255, 255, 255, 20));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(255, 255, 255, 0));
            }
        });
        topPanel.add(closeButton, BorderLayout.EAST);

        JLabel userLabel = new JLabel("Welcome, User");
        userLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        userLabel.setForeground(TEXT_COLOR);
        topPanel.add(userLabel, BorderLayout.WEST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Sidebar with navigation
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(SIDEBAR_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        sidebar.setPreferredSize(new Dimension(250, 0));

        // Sidebar header
        JLabel sidebarTitle = new JLabel("Furnish Studio");
        sidebarTitle.setFont(new Font("Montserrat", Font.BOLD, 20));
        sidebarTitle.setForeground(TEXT_COLOR);
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebar.add(sidebarTitle);

        String[] menuItems = {"Room Designer", "Furniture Catalog", "My Designs", "Profile"};
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            menuButton.addActionListener(e -> {
                if (selectedMenuButton != null) {
                    selectedMenuButton.setBackground(SIDEBAR_COLOR);
                }
                menuButton.setBackground(MENU_SELECTED);
                selectedMenuButton = menuButton;
                switchPanel(item);
            });
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Add some spacing at the bottom
        sidebar.add(Box.createVerticalGlue());

        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize panels
        contentPanel.add(createRoomDesignerPanel(), "Room Designer");
        contentPanel.add(createFurnitureCatalogPanel(), "Furniture Catalog");
        contentPanel.add(createMyDesignsPanel(), "My Designs");
        contentPanel.add(createProfilePanel(), "Profile");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Select first menu item by default
        if (sidebar.getComponentCount() > 1) {
            JButton firstButton = (JButton) sidebar.getComponent(1);
            firstButton.setBackground(MENU_SELECTED);
            selectedMenuButton = firstButton;
            switchPanel("Room Designer");
        }
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Montserrat", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_COLOR);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedMenuButton) {
                    button.setBackground(MENU_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedMenuButton) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return button;
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel title = new JLabel("Furniture Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Add furniture catalog panel
        FurnitureCatalogPanel catalogPanel = new FurnitureCatalogPanel();
        panel.add(catalogPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createMyDesignsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        
        // Add my designs panel
        MyDesignsPanel designsPanel = new MyDesignsPanel();
        panel.add(designsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel title = new JLabel("User Management", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Add profile panel
        ProfilePanel profilePanel = new ProfilePanel();
        panel.add(profilePanel, BorderLayout.CENTER);
        
        return panel;
    }
} 