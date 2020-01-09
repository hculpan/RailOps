package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class Route extends BaseModel {
    private String name;
    private int railroadId;

    private final List<Integer> stops = new ArrayList<>();

    private int switchListId;

    public Route(String name, int railroad) {
        setName(name);
        setRailroadId(railroad);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRailroadId() {
        return railroadId;
    }

    public void setRailroadId(int railroadId) {
        this.railroadId = railroadId;
    }

    public List<Integer> getStops() {
        return stops;
    }

    public int getSwitchListId() {
        return switchListId;
    }

    public void setSwitchListId(int switchListId) {
        this.switchListId = switchListId;
    }
}
