package org.furnish.ui;

import org.furnish.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VisualizationPanel extends JPanel {
    private Design design;
    private boolean is3DView = false;
    private FurnitureDesignApp parent;
    private Furniture draggedFurniture;
    private Point dragStartPoint;
    private double rotationX = 0;
    private double rotationY = 0;
    private double lastMouseX, lastMouseY;
    private float zoomFactor = 1.0f;

    public VisualizationPanel(FurnitureDesignApp parent) {
        this.parent = parent;
        initializePanel();
        setupMouseListeners();
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(1000, 900));
    }

    public void zoomIn() {
        zoomFactor *= 1.1f;
        repaint();
    }

    public void zoomOut() {
        zoomFactor /= 1.1f;
        repaint();
    }

    public void resetView() {
        zoomFactor = 1.0f;
        repaint();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedFurniture = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }
        });
    }

    private void handleMousePress(MouseEvent e) {
        dragStartPoint = e.getPoint();
        lastMouseX = e.getX();
        lastMouseY = e.getY();

        if (design == null)
            return;

        if (is3DView) {
            handle3DSelection(e);
        } else {
            handle2DSelection(e);
        }
    }

    private void handle3DSelection(MouseEvent e) {
        double scale = getScaleFactor();
        int offsetX = getWidth() / 2;
        int offsetY = getHeight() / 2;

        for (Furniture f : design.getFurnitureList()) {
            Point center = project(
                    f.getX() + f.getWidth() / 2,
                    f.getHeight() / 2,
                    f.getZ() + f.getDepth() / 2,
                    scale, offsetX, offsetY);
            double distance = Math.sqrt(
                    Math.pow(e.getX() - center.x, 2) +
                            Math.pow(e.getY() - center.y, 2));
            if (distance < 20) {
                draggedFurniture = f;
                parent.setSelectedFurniture(f);
                break;
            }
        }
    }

    private void handle2DSelection(MouseEvent e) {
        double scale = Math.min(
                getWidth() / design.getRoom().getLength(),
                getHeight() / design.getRoom().getWidth());

        for (Furniture f : design.getFurnitureList()) {
            int x = (int) (f.getX() * scale);
            int z = (int) (f.getZ() * scale);
            int w = (int) (f.getWidth() * scale);
            int d = (int) (f.getDepth() * scale);

            if (e.getX() >= x && e.getX() <= x + w &&
                    e.getY() >= z && e.getY() <= z + d) {
                draggedFurniture = f;
                dragStartPoint = new Point(e.getX() - x, e.getY() - z);
                parent.setSelectedFurniture(f);
                break;
            }
        }
    }

    private void handleMouseDrag(MouseEvent e) {
        if (is3DView && draggedFurniture == null) {
            double deltaX = e.getX() - lastMouseX;
            double deltaY = e.getY() - lastMouseY;
            rotationY += deltaX * 0.01;
            rotationX += deltaY * 0.01;
            repaint();
        } else if (draggedFurniture != null && !is3DView) {
            double scale = Math.min(
                    getWidth() / design.getRoom().getLength(),
                    getHeight() / design.getRoom().getWidth());
            double newX = (e.getX() - dragStartPoint.x) / scale;
            double newZ = (e.getY() - dragStartPoint.y) / scale;

            draggedFurniture.setX(Math.max(0, Math.min(
                    newX,
                    design.getRoom().getLength() - draggedFurniture.getWidth())));
            draggedFurniture.setZ(Math.max(0, Math.min(
                    newZ,
                    design.getRoom().getWidth() - draggedFurniture.getDepth())));

            parent.propertiesPanel.update(draggedFurniture);
            repaint();
        }

        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    private double getScaleFactor() {
        return Math.min(
                getWidth() / (design.getRoom().getLength() + design.getRoom().getWidth()),
                getHeight() / (design.getRoom().getHeight() + design.getRoom().getWidth())) * 0.5;
    }

    public void setDesign(Design design) {
        this.design = design;
        rotationX = 0;
        rotationY = 0;
        repaint();
    }

    public void set3DView(boolean is3DView) {
        this.is3DView = is3DView;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (design == null)
            return;

        if (is3DView) {
            draw3D(g2d);
        } else {
            draw2D(g2d);
        }
    }

    private void draw2D(Graphics2D g2d) {
        Room room = design.getRoom();
        double scale = Math.min(
                getWidth() / room.getLength(),
                getHeight() / room.getWidth());

        // Draw floor
        g2d.setColor(room.getFloorColor());
        g2d.fillRect(0, 0,
                (int) (room.getLength() * scale),
                (int) (room.getWidth() * scale));

        // Draw furniture
        for (Furniture f : design.getFurnitureList()) {
            g2d.setColor(f.getColor());
            int x = (int) (f.getX() * scale);
            int z = (int) (f.getZ() * scale);
            int w = (int) (f.getWidth() * scale);
            int d = (int) (f.getDepth() * scale);

            g2d.fillRect(x, z, w, d);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, z, w, d);
        }
    }

    private void draw3D(Graphics2D g2d) {
        Room room = design.getRoom();
        double scale = getScaleFactor();
        int offsetX = getWidth() / 2;
        int offsetY = getHeight() / 2;

        g2d.translate(offsetX, offsetY);
        g2d.rotate(rotationY); // Y-axis rotation
        g2d.rotate(rotationX); // X-axis rotation
        g2d.translate(-offsetX, -offsetY);

        drawFloor(g2d, room, scale, offsetX, offsetY);
        drawWalls(g2d, room, scale, offsetX, offsetY);
        drawFurniture(g2d, scale, offsetX, offsetY);
    }

    private void drawFloor(Graphics2D g2d, Room room,
            double scale, int offsetX, int offsetY) {
        Point[] floorPoints = {
                project(0, 0, 0, scale, offsetX, offsetY),
                project(room.getLength(), 0, 0, scale, offsetX, offsetY),
                project(room.getLength(), 0, room.getWidth(), scale, offsetX, offsetY),
                project(0, 0, room.getWidth(), scale, offsetX, offsetY)
        };

        g2d.setColor(room.getFloorColor());
        g2d.fillPolygon(
                new int[] { floorPoints[0].x, floorPoints[1].x, floorPoints[2].x, floorPoints[3].x },
                new int[] { floorPoints[0].y, floorPoints[1].y, floorPoints[2].y, floorPoints[3].y },
                4);
    }

    private void drawWalls(Graphics2D g2d, Room room,
            double scale, int offsetX, int offsetY) {
        g2d.setColor(room.getWallColor());

        // Wall 1
        Point[] wall1Points = {
                project(0, 0, 0, scale, offsetX, offsetY),
                project(room.getLength(), 0, 0, scale, offsetX, offsetY),
                project(room.getLength(), room.getHeight(), 0, scale, offsetX, offsetY),
                project(0, room.getHeight(), 0, scale, offsetX, offsetY)
        };
        g2d.fillPolygon(
                new int[] { wall1Points[0].x, wall1Points[1].x, wall1Points[2].x, wall1Points[3].x },
                new int[] { wall1Points[0].y, wall1Points[1].y, wall1Points[2].y, wall1Points[3].y },
                4);

        // Wall 2
        Point[] wall2Points = {
                project(0, 0, 0, scale, offsetX, offsetY),
                project(0, 0, room.getWidth(), scale, offsetX, offsetY),
                project(0, room.getHeight(), room.getWidth(), scale, offsetX, offsetY),
                project(0, room.getHeight(), 0, scale, offsetX, offsetY)
        };
        g2d.fillPolygon(
                new int[] { wall2Points[0].x, wall2Points[1].x, wall2Points[2].x, wall2Points[3].x },
                new int[] { wall2Points[0].y, wall2Points[1].y, wall2Points[2].y, wall2Points[3].y },
                4);
    }

    private void drawFurniture(Graphics2D g2d,
            double scale, int offsetX, int offsetY) {
        for (Furniture f : design.getFurnitureList()) {
            if (f.getType().equals("Chair")) {
                drawChair3D(g2d, f, scale, offsetX, offsetY);
            } else if (f.getType().equals("Table")) {
                drawTable3D(g2d, f, scale, offsetX, offsetY);
            }
        }
    }

    private void drawChair3D(Graphics2D g2d, Furniture f,
            double scale, int offsetX, int offsetY) {
        Color darkColor = f.getColor().darker();

        // Seat
        drawBox3D(g2d,
                f.getX(), f.getHeight() * 0.6, f.getZ(),
                f.getWidth(), f.getDepth(), f.getHeight() * 0.1,
                scale, offsetX, offsetY, f.getColor());

        // Back
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.1, f.getHeight() * 0.7, f.getZ() - f.getDepth() * 0.2,
                f.getWidth() * 0.8, f.getHeight() * 0.05, f.getHeight() * 0.4,
                scale, offsetX, offsetY, darkColor);

        // Legs
        double legWidth = f.getWidth() * 0.1;
        double legDepth = f.getDepth() * 0.1;

        // Front legs
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.1, 0, f.getZ() + f.getDepth() * 0.1,
                legWidth, legDepth, f.getHeight() * 0.6,
                scale, offsetX, offsetY, darkColor);
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.8, 0, f.getZ() + f.getDepth() * 0.1,
                legWidth, legDepth, f.getHeight() * 0.6,
                scale, offsetX, offsetY, darkColor);

        // Back legs
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.1, 0, f.getZ() + f.getDepth() * 0.8,
                legWidth, legDepth, f.getHeight(),
                scale, offsetX, offsetY, darkColor);
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.8, 0, f.getZ() + f.getDepth() * 0.8,
                legWidth, legDepth, f.getHeight(),
                scale, offsetX, offsetY, darkColor);
    }

    private void drawTable3D(Graphics2D g2d, Furniture f,
            double scale, int offsetX, int offsetY) {
        Color darkColor = f.getColor().darker();

        // Table top
        drawBox3D(g2d,
                f.getX(), f.getHeight() * 0.8, f.getZ(),
                f.getWidth(), f.getDepth(), f.getHeight() * 0.05,
                scale, offsetX, offsetY, f.getColor());

        // Legs
        double legWidth = f.getWidth() * 0.1;
        double legDepth = f.getDepth() * 0.1;

        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.1, 0, f.getZ() + f.getDepth() * 0.1,
                legWidth, legDepth, f.getHeight() * 0.8,
                scale, offsetX, offsetY, darkColor);
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.8, 0, f.getZ() + f.getDepth() * 0.1,
                legWidth, legDepth, f.getHeight() * 0.8,
                scale, offsetX, offsetY, darkColor);
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.1, 0, f.getZ() + f.getDepth() * 0.8,
                legWidth, legDepth, f.getHeight() * 0.8,
                scale, offsetX, offsetY, darkColor);
        drawBox3D(g2d,
                f.getX() + f.getWidth() * 0.8, 0, f.getZ() + f.getDepth() * 0.8,
                legWidth, legDepth, f.getHeight() * 0.8,
                scale, offsetX, offsetY, darkColor);
    }

    private void drawBox3D(Graphics2D g2d,
            double x, double y, double z,
            double w, double d, double h,
            double scale, int offsetX, int offsetY,
            Color color) {
        Point[] points = new Point[8];
        points[0] = project(x, y, z, scale, offsetX, offsetY);
        points[1] = project(x + w, y, z, scale, offsetX, offsetY);
        points[2] = project(x + w, y, z + d, scale, offsetX, offsetY);
        points[3] = project(x, y, z + d, scale, offsetX, offsetY);
        points[4] = project(x, y + h, z, scale, offsetX, offsetY);
        points[5] = project(x + w, y + h, z, scale, offsetX, offsetY);
        points[6] = project(x + w, y + h, z + d, scale, offsetX, offsetY);
        points[7] = project(x, y + h, z + d, scale, offsetX, offsetY);

        Color topColor = color.brighter();
        Color sideColor1 = color;
        Color sideColor2 = color.darker();

        // Front face
        g2d.setColor(sideColor1);
        g2d.fillPolygon(
                new int[] { points[0].x, points[1].x, points[5].x, points[4].x },
                new int[] { points[0].y, points[1].y, points[5].y, points[4].y },
                4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(
                new int[] { points[0].x, points[1].x, points[5].x, points[4].x },
                new int[] { points[0].y, points[1].y, points[5].y, points[4].y },
                4);

        // Top face
        g2d.setColor(topColor);
        g2d.fillPolygon(
                new int[] { points[4].x, points[5].x, points[6].x, points[7].x },
                new int[] { points[4].y, points[5].y, points[6].y, points[7].y },
                4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(
                new int[] { points[4].x, points[5].x, points[6].x, points[7].x },
                new int[] { points[4].y, points[5].y, points[6].y, points[7].y },
                4);

        // Side face
        g2d.setColor(sideColor2);
        g2d.fillPolygon(
                new int[] { points[1].x, points[2].x, points[6].x, points[5].x },
                new int[] { points[1].y, points[2].y, points[6].y, points[5].y },
                4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(
                new int[] { points[1].x, points[2].x, points[6].x, points[5].x },
                new int[] { points[1].y, points[2].y, points[6].y, points[5].y },
                4);
    }

    private Point project(double x, double y, double z,
            double scale, int offsetX, int offsetY) {
        double isoX = (x - z) * Math.cos(Math.toRadians(30)) * scale;
        double isoY = ((x + z) * Math.sin(Math.toRadians(30)) - y) * scale;
        return new Point((int) isoX + offsetX, (int) isoY + offsetY);
    }
}