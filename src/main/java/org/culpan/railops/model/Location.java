package org.culpan.railops.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private int id;

    private final SimpleStringProperty name = new SimpleStringProperty();
    private final List<String> carsTypes = new ArrayList<>();
    private final List<String> railroads = new ArrayList<>();
    private final SimpleBooleanProperty staging = new SimpleBooleanProperty();

    public Location() {};

    public Location(int id, String name) {
        setId(id);
        setName(name);
    }

    public Location(String name) {
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getCarsTypes() {
        return carsTypes;
    }

    public List<String> getRailroads() {
        return railroads;
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

    public boolean isStaging() {
        return staging.get();
    }

    public SimpleBooleanProperty stagingProperty() {
        return staging;
    }

    public void setStaging(boolean staging) {
        this.staging.set(staging);
    }
}
