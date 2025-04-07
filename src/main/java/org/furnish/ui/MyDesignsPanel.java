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
    private JPanel designsPanel;
    private JScrollPane scrollPane;

    public MyDesignsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(60, 60, 90));

        // Title
        JLabel title = new JLabel("My Designs", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Designs panel with grid layout
        designsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        designsPanel.setBackground(new Color(60, 60, 90));
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
            noDesignsLabel.setForeground(Color.WHITE);
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
        card.setBackground(new Color(80, 80, 110));
        card.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 130), 2));
        card.setPreferredSize(new Dimension(250, 200));

        // Design preview (placeholder for now)
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(120, 120, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        previewPanel.setPreferredSize(new Dimension(250, 150));

        // Design info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(80, 80, 110));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(design.getName());
        nameLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        infoPanel.add(nameLabel, BorderLayout.CENTER);

        JButton openButton = new JButton("Open");
        openButton.setFont(new Font("Montserrat", Font.PLAIN, 12));
        openButton.setBackground(new Color(92, 184, 92));
        openButton.setForeground(Color.WHITE);
        openButton.setFocusPainted(false);
        openButton.addActionListener(e -> openDesign(design));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Montserrat", Font.PLAIN, 12));
        deleteButton.setBackground(new Color(217, 83, 79));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteDesign(design));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(80, 80, 110));
        buttonPanel.add(openButton);
        buttonPanel.add(deleteButton);
        infoPanel.add(buttonPanel, BorderLayout.EAST);

        card.add(previewPanel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
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