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
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class RoomDesignerPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(60, 60, 90);
    private static final Color TOOLBAR_COLOR = new Color(42, 42, 74);
    private static final Color BUTTON_COLOR = new Color(46, 204, 113);
    private static final Color BUTTON_HOVER = new Color(39, 174, 96);
    private static final Color TEXT_COLOR = new Color(236, 240, 241);
    private static final Color GRID_COLOR = new Color(100, 100, 130);
    private static final Color ROOM_COLOR = new Color(80, 80, 110);
    private static final Color FURNITURE_COLOR = new Color(52, 152, 219);
    private static final Color SELECTED_COLOR = new Color(241, 196, 15);

    private List<FurnitureItem> furnitureItems;
    private FurnitureItem selectedItem;
    private Point dragStart;
    private Room room;
    private JComboBox<String> furnitureTypeCombo;
    private JButton addFurnitureButton;
    private JButton saveDesignButton;
    private double zoomFactor = 1.0;
    private Point2D viewportPosition = new Point2D.Double(0, 0);
    private Point lastMousePosition;

    public RoomDesignerPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        // Initialize room and furniture items
        room = new Room(800, 600);
        furnitureItems = new ArrayList<>();
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Add mouse listeners
        addMouseListeners();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBackground(TOOLBAR_COLOR);
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        String[] furnitureTypes = {"Sofa", "Table", "Chair", "Bed", "Wardrobe", "Bookshelf"};
        furnitureTypeCombo = new JComboBox<>(furnitureTypes);
        furnitureTypeCombo.setFont(new Font("Montserrat", Font.PLAIN, 14));
        furnitureTypeCombo.setBackground(new Color(60, 60, 90));
        furnitureTypeCombo.setForeground(TEXT_COLOR);
        furnitureTypeCombo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        furnitureTypeCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                comboBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            }
        });
        
        addFurnitureButton = createStyledButton("Add Furniture");
        addFurnitureButton.addActionListener(e -> addFurniture());
        
        saveDesignButton = createStyledButton("Save Design");
        saveDesignButton.addActionListener(e -> saveDesign());
        
        toolbar.add(furnitureTypeCombo);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(addFurnitureButton);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(saveDesignButton);
        
        return toolbar;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(BUTTON_COLOR);
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
    
    private void addMouseListeners() {
        // For furniture selection and dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                lastMousePosition = e.getPoint();
                
                // Convert mouse coordinates to zoomed space
                Point2D transformedPoint = transformPoint(e.getPoint());
                selectFurniture(new Point((int)transformedPoint.getX(), (int)transformedPoint.getY()));
                
                if (selectedItem != null) {
                    dragStart = new Point((int)transformedPoint.getX(), (int)transformedPoint.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedItem = null;
                dragStart = null;
            }
        });
        
        // For dragging furniture
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && dragStart != null) {
                    Point2D transformedPoint = transformPoint(e.getPoint());
                    int dx = (int)transformedPoint.getX() - dragStart.x;
                    int dy = (int)transformedPoint.getY() - dragStart.y;
                    selectedItem.move(dx, dy);
                    dragStart = new Point((int)transformedPoint.getX(), (int)transformedPoint.getY());
                    repaint();
                } else if (e.isControlDown() || e.isShiftDown()) {
                    // Pan the view when Ctrl or Shift is held while dragging
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;
                    viewportPosition.setLocation(
                        viewportPosition.getX() + dx / zoomFactor,
                        viewportPosition.getY() + dy / zoomFactor
                    );
                    lastMousePosition = e.getPoint();
                    repaint();
                }
            }
        });

        // For zooming
        addMouseWheelListener(e -> {
            handleZoom(e);
        });
    }
    
    private void handleZoom(MouseWheelEvent e) {
        double oldZoom = zoomFactor;
        int notches = e.getWheelRotation();
        
        // Calculate zoom factor
        if (notches < 0) {
            // Zoom in
            zoomFactor = Math.min(5.0, zoomFactor * 1.1);
        } else {
            // Zoom out
            zoomFactor = Math.max(0.2, zoomFactor / 1.1);
        }
        
        // Get mouse position in untransformed coordinates
        Point mousePos = e.getPoint();
        
        // Adjust viewport position to zoom toward mouse pointer
        viewportPosition.setLocation(
            viewportPosition.getX() + (mousePos.x / oldZoom - mousePos.x / zoomFactor),
            viewportPosition.getY() + (mousePos.y / oldZoom - mousePos.y / zoomFactor)
        );
        
        repaint();
    }
    
    private Point2D transformPoint(Point point) {
        return new Point2D.Double(
            point.x / zoomFactor + viewportPosition.getX(),
            point.y / zoomFactor + viewportPosition.getY()
        );
    }
    
    private void addFurniture() {
        String type = (String) furnitureTypeCombo.getSelectedItem();
        FurnitureItem item = new FurnitureItem(type, 100, 100);
        
        // Position new furniture at the center of the view
        Point2D centerView = new Point2D.Double(
            -viewportPosition.getX() + getWidth() / (2 * zoomFactor),
            -viewportPosition.getY() + getHeight() / (2 * zoomFactor)
        );
        
        item.setPosition((int)centerView.getX(), (int)centerView.getY());
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
        JOptionPane.showMessageDialog(this, "Design saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Save original transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply zoom and pan transformations
        g2d.translate(-viewportPosition.getX() * zoomFactor, -viewportPosition.getY() * zoomFactor);
        g2d.scale(zoomFactor, zoomFactor);
        
        // Draw room
        room.draw(g2d);
        
        // Draw furniture items
        for (FurnitureItem item : furnitureItems) {
            item.draw(g2d, item == selectedItem);
        }
        
        // Restore original transform
        g2d.setTransform(originalTransform);
        
        // Draw zoom level indicator
        drawZoomIndicator(g2d);
    }
    
    private void drawZoomIndicator(Graphics2D g2d) {
        String zoomText = String.format("%d%%", (int)(zoomFactor * 100));
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Montserrat", Font.BOLD, 14));
        
        // Draw background
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(10, 10, 60, 30, 8, 8);
        
        // Draw text
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(zoomText, 20, 30);
    }
    
    private class Room {
        private int width;
        private int height;
        
        public Room(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public void draw(Graphics2D g2d) {
            // Draw room background
            g2d.setColor(ROOM_COLOR);
            g2d.fillRoundRect(50, 50, width, height, 20, 20);
            
            // Draw grid
            g2d.setColor(GRID_COLOR);
            int gridSize = 50;
            for (int x = 50; x <= width + 50; x += gridSize) {
                g2d.drawLine(x, 50, x, height + 50);
            }
            for (int y = 50; y <= height + 50; y += gridSize) {
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
        }
        
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }
        
        public boolean contains(Point point) {
            return new RoundRectangle2D.Float(x, y, width, height, 10, 10).contains(point);
        }
        
        public void draw(Graphics2D g2d, boolean selected) {
            // Draw furniture item
            if (selected) {
                g2d.setColor(SELECTED_COLOR);
            } else {
                g2d.setColor(FURNITURE_COLOR);
            }
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            // Draw type label
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Montserrat", Font.PLAIN, 12));
            String label = type;
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, x + (width - labelWidth) / 2, y + height / 2);
            
            // Draw selection border
            if (selected) {
                g2d.setColor(TEXT_COLOR);
                g2d.setStroke(new java.awt.BasicStroke(2));
                g2d.drawRoundRect(x, y, width, height, 10, 10);
            }
        }
    }
} 