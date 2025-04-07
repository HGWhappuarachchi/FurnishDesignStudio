package org.furnish.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Design implements Serializable {
    private Room room;
    private List<Furniture> furnitureList;
    private boolean modified;

    public Design(Room room) {
        this.room = room;
        this.furnitureList = new ArrayList<>();
        this.modified = false;
    }

    public Room getRoom() {
        return room;
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public void addFurniture(Furniture f) {
        furnitureList.add(f);
        modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}