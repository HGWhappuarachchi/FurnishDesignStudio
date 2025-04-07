package org.furnish.core;

import java.awt.*;
import java.io.Serializable;

public class Furniture implements Serializable {
    private String type;
    private double xPosition, zPosition;
    private double width, depth, height;
    private Color color;
    private float shadeFactor = 1.0f;

    public Furniture(String type, double xPosition, double zPosition,
            double width, double depth, double height, Color color) {
        this.type = type;
        this.xPosition = xPosition;
        this.zPosition = zPosition;
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.color = color;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public double getX() {
        return xPosition;
    }

    public double getZ() {
        return zPosition;
    }

    public double getWidth() {
        return width;
    }

    public double getDepth() {
        return depth;
    }

    public double getHeight() {
        return height;
    }

    public Color getColor() {
        return new Color(
                (int) (color.getRed() * shadeFactor),
                (int) (color.getGreen() * shadeFactor),
                (int) (color.getBlue() * shadeFactor));
    }

    public float getShadeFactor() {
        return shadeFactor;
    }

    public void setX(double x) {
        this.xPosition = x;
    }

    public void setZ(double z) {
        this.zPosition = z;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShadeFactor(float factor) {
        this.shadeFactor = Math.max(0.1f, Math.min(1.0f, factor));
    }
}