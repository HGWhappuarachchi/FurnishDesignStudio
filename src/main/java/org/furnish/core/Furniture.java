package org.furnish.core;

import java.awt.Color;
import java.io.Serializable;

public class Furniture implements Serializable {
    private String type;
    private double xPosition, zPosition;
    private double width, depth, height;
    private Color color;
    private float shadeFactor = 1.0f;
    private double rotation = 0.0; // Rotation in degrees
    private String texturePath = null;
    private float reflectivity = 0.0f;
    private float specularIntensity = 0.0f;
    private boolean isCollidable = true;

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

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation % 360.0;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = Math.max(0.0f, Math.min(1.0f, reflectivity));
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = Math.max(0.0f, Math.min(1.0f, specularIntensity));
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public void setCollidable(boolean collidable) {
        isCollidable = collidable;
    }

    public boolean collidesWith(Furniture other) {
        if (!isCollidable || !other.isCollidable) {
            return false;
        }

        // Simple axis-aligned bounding box collision detection
        double thisLeft = xPosition - width/2;
        double thisRight = xPosition + width/2;
        double thisFront = zPosition - depth/2;
        double thisBack = zPosition + depth/2;

        double otherLeft = other.xPosition - other.width/2;
        double otherRight = other.xPosition + other.width/2;
        double otherFront = other.zPosition - other.depth/2;
        double otherBack = other.zPosition + other.depth/2;

        return !(thisRight < otherLeft || thisLeft > otherRight ||
                thisBack < otherFront || thisFront > otherBack);
    }
}