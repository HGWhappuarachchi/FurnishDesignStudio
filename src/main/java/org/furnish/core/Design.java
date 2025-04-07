package org.furnish.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Design implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Furniture> furnitureList;
    private boolean modified;
    private Room room;

    public Design() {
        this.furnitureList = new ArrayList<>();
        this.modified = false;
    }

    public Design(Room room) {
        this();
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.modified = true;
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public void setFurnitureList(List<Furniture> furnitureList) {
        this.furnitureList = furnitureList;
        this.modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void addFurniture(Furniture furniture) {
        furnitureList.add(furniture);
        modified = true;
    }

    public void removeFurniture(Furniture furniture) {
        furnitureList.remove(furniture);
        modified = true;
    }
}