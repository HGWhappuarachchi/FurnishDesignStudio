package org.furnish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.furnish.core.Design;
import org.furnish.core.DesignManager;

public class MyDesignsPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(60, 60, 90);
    private static final Color CARD_BACKGROUND = new Color(80, 80, 110);
    private static final Color CARD_BORDER = new Color(100, 100, 130);
    private static final Color CARD_HOVER = new Color(90, 90, 120);
    private static final Color TEXT_COLOR = new Color(236, 240, 241);
    private static final Color BUTTON_OPEN_COLOR = new Color(46, 204, 113);
    private static final Color BUTTON_DELETE_COLOR = new Color(231, 76, 60);
    private static final Color BUTTON_HOVER = new Color(52, 73, 94);

    private JPanel designsPanel;
    private JScrollPane scrollPane;

    public MyDesignsPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title
        JLabel title = new JLabel("My Designs", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Designs panel with grid layout
        designsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        designsPanel.setBackground(BACKGROUND_COLOR);
        designsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Scroll pane for designs
        scrollPane = new JScrollPane(designsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display designs
        loadDesigns();
    }

    private void loadDesigns() {
        designsPanel.removeAll();
        List<Design> designs = DesignManager.loadAllDesigns();

        if (designs.isEmpty()) {
            JLabel noDesignsLabel = new JLabel("No saved designs yet", SwingConstants.CENTER);
            noDesignsLabel.setFont(new Font("Montserrat", Font.PLAIN, 16));
            noDesignsLabel.setForeground(TEXT_COLOR);
            designsPanel.add(noDesignsLabel);
        } else {
            for (Design design : designs) {
                JPanel designCard = createDesignCard(design);
                designsPanel.add(designCard);
            }
        }

        designsPanel.revalidate();
        designsPanel.repaint();
    }

    private JPanel createDesignCard(Design design) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
        card.setPreferredSize(new Dimension(250, 200));

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND);
            }
        });

        // Design preview (placeholder for now)
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Draw gradient background
                g2d.setColor(new Color(120, 120, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw design name in the center
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Montserrat", Font.BOLD, 16));
                String text = design.getName();
                int textWidth = g2d.getFontMetrics().stringWidth(text);
                g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
            }
        };
        previewPanel.setPreferredSize(new Dimension(250, 150));

        // Design info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel nameLabel = new JLabel(design.getName());
        nameLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        infoPanel.add(nameLabel, BorderLayout.CENTER);

        JButton openButton = createStyledButton("Open", BUTTON_OPEN_COLOR);
        openButton.addActionListener(e -> openDesign(design));

        JButton deleteButton = createStyledButton("Delete", BUTTON_DELETE_COLOR);
        deleteButton.addActionListener(e -> deleteDesign(design));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.add(openButton);
        buttonPanel.add(deleteButton);
        infoPanel.add(buttonPanel, BorderLayout.EAST);

        card.add(previewPanel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
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
        
        button.setFont(new Font("Montserrat", Font.BOLD, 12));
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        return button;
    }

    private void openDesign(Design design) {
        // Open the design in FurnitureDesignApp
        SwingUtilities.invokeLater(() -> {
            FurnitureDesignApp app = new FurnitureDesignApp();
            app.setDesign(design);
            app.setVisible(true);
            
            // Close the current window if it's a JFrame
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                window.dispose();
            }
        });
    }

    private void deleteDesign(Design design) {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this design?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            DesignManager.deleteDesign(design.getName());
            loadDesigns(); // Reload the designs
        }
    }
} 