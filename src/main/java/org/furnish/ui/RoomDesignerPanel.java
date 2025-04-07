package org.furnish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class RoomDesignerPanel extends JPanel {
    private List<FurnitureItem> furnitureItems;
    private FurnitureItem selectedItem;
    private Point dragStart;
    private Room room;
    private JComboBox<String> furnitureTypeCombo;
    private JButton addFurnitureButton;
    private JButton saveDesignButton;

    public RoomDesignerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(60, 60, 90));
        
        // Initialize room and furniture items
        room = new Room(800, 600);
        furnitureItems = new ArrayList<>();
        
        // Create toolbar
        JPanel toolbar = new JPanel();
        toolbar.setBackground(new Color(42, 42, 74));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] furnitureTypes = {"Sofa", "Table", "Chair", "Bed", "Wardrobe", "Bookshelf"};
        furnitureTypeCombo = new JComboBox<>(furnitureTypes);
        furnitureTypeCombo.setFont(new Font("Montserrat", Font.PLAIN, 14));
        
        addFurnitureButton = new JButton("Add Furniture");
        addFurnitureButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        addFurnitureButton.setBackground(new Color(92, 184, 92));
        addFurnitureButton.setForeground(Color.WHITE);
        addFurnitureButton.addActionListener(e -> addFurniture());
        
        saveDesignButton = new JButton("Save Design");
        saveDesignButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        saveDesignButton.setBackground(new Color(92, 184, 92));
        saveDesignButton.setForeground(Color.WHITE);
        saveDesignButton.addActionListener(e -> saveDesign());
        
        toolbar.add(furnitureTypeCombo);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(addFurnitureButton);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(saveDesignButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Add mouse listeners for dragging furniture
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectFurniture(e.getPoint());
                if (selectedItem != null) {
                    dragStart = e.getPoint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedItem = null;
                dragStart = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    selectedItem.move(dx, dy);
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
    }
    
    private void addFurniture() {
        String type = (String) furnitureTypeCombo.getSelectedItem();
        FurnitureItem item = new FurnitureItem(type, 100, 100);
        furnitureItems.add(item);
        repaint();
    }
    
    private void selectFurniture(Point point) {
        selectedItem = null;
        for (FurnitureItem item : furnitureItems) {
            if (item.contains(point)) {
                selectedItem = item;
                break;
            }
        }
        repaint();
    }
    
    private void saveDesign() {
        // TODO: Implement design saving functionality
        JOptionPane.showMessageDialog(this, "Design saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw room
        room.draw(g2d);
        
        // Draw furniture items
        for (FurnitureItem item : furnitureItems) {
            item.draw(g2d, item == selectedItem);
        }
    }
    
    private class Room {
        private int width;
        private int height;
        
        public Room(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(new Color(80, 80, 110));
            g2d.fillRect(50, 50, width, height);
            
            // Draw grid
            g2d.setColor(new Color(100, 100, 130));
            for (int x = 50; x <= width + 50; x += 50) {
                g2d.drawLine(x, 50, x, height + 50);
            }
            for (int y = 50; y <= height + 50; y += 50) {
                g2d.drawLine(50, y, width + 50, y);
            }
        }
    }
    
    private class FurnitureItem {
        private String type;
        private int x;
        private int y;
        private int width;
        private int height;
        
        public FurnitureItem(String type, int width, int height) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.x = 100;
            this.y = 100;
        }
        
        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }
        
        public boolean contains(Point point) {
            return new Rectangle2D.Float(x, y, width, height).contains(point);
        }
        
        public void draw(Graphics2D g2d, boolean selected) {
            g2d.setColor(selected ? new Color(92, 184, 92) : new Color(120, 120, 150));
            g2d.fillRect(x, y, width, height);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Montserrat", Font.PLAIN, 12));
            g2d.drawString(type, x + 5, y + 20);
        }
    }
} 