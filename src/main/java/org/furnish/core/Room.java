package org.furnish.core;

import java.awt.*;
import java.io.Serializable;

public class Room implements Serializable {
    private double length, width, height;
    private Color floorColor, wallColor;

    public Room(double length, double width, double height,
            Color floorColor, Color wallColor) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    public Color getWallColor() {
        return wallColor;
    }
}