package org.culpan.railops.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int id;

    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty railroad = new SimpleStringProperty();

    private final List<Location> stops = new ArrayList<>();

    private int switchListId;

    public Route(String name, String railroad) {
        setName(name);
        setRailroad(railroad);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getRailroad() {
        return railroad.get();
    }

    public SimpleStringProperty railroadProperty() {
        return railroad;
    }

    public void setRailroad(String railroad) {
        this.railroad.set(railroad);
    }

    public List<Location> getStops() {
        return stops;
    }

    public int getSwitchListId() {
        return switchListId;
    }

    public void setSwitchListId(int switchListId) {
        this.switchListId = switchListId;
    }
}
