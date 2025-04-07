package org.furnish.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Design implements Serializable {
    private Room room;
    private List<Furniture> furnitureList;

    public Design(Room room) {
        this.room = room;
        this.furnitureList = new ArrayList<>();
    }

    public Room getRoom() {
        return room;
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public void addFurniture(Furniture f) {
        furnitureList.add(f);
    }
}