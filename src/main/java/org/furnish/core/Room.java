package org.furnish.core;

import java.awt.Color;
import java.io.Serializable;

public class Room implements Serializable {
    private double length, width, height;
    private Color floorColor, wallColor;
    private String floorTexturePath = null;
    private String wallTexturePath = null;
    private float ambientLight = 0.5f;
    private float[] lightPosition = {0.0f, (float)height, 0.0f}; // Default light position at ceiling center
    private float lightIntensity = 1.0f;
    private Color lightColor = Color.WHITE;
    
    // New position and rotation properties
    private double xPosition = 0.0;
    private double yPosition = 0.0;
    private double zPosition = 0.0;
    private double rotationX = 0.0; // Rotation around X axis in degrees
    private double rotationY = 0.0; // Rotation around Y axis in degrees
    private double rotationZ = 0.0; // Rotation around Z axis in degrees

    public Room(double length, double width, double height,
            Color floorColor, Color wallColor) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
        this.lightPosition[1] = (float)height; // Update light position after height is set
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

    public String getFloorTexturePath() {
        return floorTexturePath;
    }

    public void setFloorTexturePath(String floorTexturePath) {
        this.floorTexturePath = floorTexturePath;
    }

    public String getWallTexturePath() {
        return wallTexturePath;
    }

    public void setWallTexturePath(String wallTexturePath) {
        this.wallTexturePath = wallTexturePath;
    }

    public float getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(float ambientLight) {
        this.ambientLight = Math.max(0.0f, Math.min(1.0f, ambientLight));
    }

    public float[] getLightPosition() {
        return lightPosition.clone();
    }

    public void setLightPosition(float x, float y, float z) {
        this.lightPosition[0] = x;
        this.lightPosition[1] = y;
        this.lightPosition[2] = z;
    }

    public float getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(float lightIntensity) {
        this.lightIntensity = Math.max(0.0f, Math.min(1.0f, lightIntensity));
    }

    public Color getLightColor() {
        return lightColor;
    }

    public void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }

    // New position getters and setters
    public double getX() {
        return xPosition;
    }

    public double getY() {
        return yPosition;
    }

    public double getZ() {
        return zPosition;
    }

    public void setPosition(double x, double y, double z) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        updateLightPosition();
    }

    public void move(double dx, double dy, double dz) {
        this.xPosition += dx;
        this.yPosition += dy;
        this.zPosition += dz;
        updateLightPosition();
    }

    // New rotation getters and setters
    public double getRotationX() {
        return rotationX;
    }

    public double getRotationY() {
        return rotationY;
    }

    public double getRotationZ() {
        return rotationZ;
    }

    public void setRotation(double x, double y, double z) {
        this.rotationX = x % 360.0;
        this.rotationY = y % 360.0;
        this.rotationZ = z % 360.0;
        updateLightPosition();
    }

    public void rotate(double dx, double dy, double dz) {
        this.rotationX = (this.rotationX + dx) % 360.0;
        this.rotationY = (this.rotationY + dy) % 360.0;
        this.rotationZ = (this.rotationZ + dz) % 360.0;
        updateLightPosition();
    }

    private void updateLightPosition() {
        // Update light position relative to room's position and rotation
        // This is a simplified version - in a real 3D engine, you'd use matrix transformations
        double cosX = Math.cos(Math.toRadians(rotationX));
        double sinX = Math.sin(Math.toRadians(rotationX));
        double cosY = Math.cos(Math.toRadians(rotationY));
        double sinY = Math.sin(Math.toRadians(rotationY));
        double cosZ = Math.cos(Math.toRadians(rotationZ));
        double sinZ = Math.sin(Math.toRadians(rotationZ));

        // Apply rotation to light position
        float rotatedX = (float)(lightPosition[0] * cosY * cosZ - lightPosition[1] * sinZ + lightPosition[2] * sinY);
        float rotatedY = (float)(lightPosition[0] * (sinX * sinY * cosZ + cosX * sinZ) + 
                               lightPosition[1] * cosX * cosZ - 
                               lightPosition[2] * sinX * cosY);
        float rotatedZ = (float)(lightPosition[0] * (sinX * sinZ - cosX * sinY * cosZ) + 
                               lightPosition[1] * cosX * sinZ + 
                               lightPosition[2] * cosX * cosY);

        // Update light position with room's position
        lightPosition[0] = (float)(rotatedX + xPosition);
        lightPosition[1] = (float)(rotatedY + yPosition + height);
        lightPosition[2] = (float)(rotatedZ + zPosition);
    }
}