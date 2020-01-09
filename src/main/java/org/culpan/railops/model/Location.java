package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class Location extends BaseModel {
    private String name;
    private boolean staging;

    private final List<String> carsTypes = new ArrayList<>();
    private final List<Integer> railroadIds = new ArrayList<>();

    public Location() {};

    public Location(int id, String name) {
        setId(id);
        setName(name);
    }

    public Location(String name) {
        setName(name);
    }

    public List<String> getCarsTypes() {
        return carsTypes;
    }

    public List<Integer> getRailroadIds() {
        return railroadIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStaging() {
        return staging;
    }

    public void setStaging(boolean staging) {
        this.staging = staging;
    }
}
