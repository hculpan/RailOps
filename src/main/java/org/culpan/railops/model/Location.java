package org.culpan.railops.model;

import org.culpan.railops.dao.CarTypesByLocationDao;
import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToMany;
import org.culpan.railops.dao.annotations.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "locations")
public class Location extends BaseModel {
    @Column
    private String name;

    @Column(name = "staging")
    private boolean staging;

    @OneToMany(foreignKeyName = "location_id", dao = CarTypesByLocationDao.class)
    private final List<CarTypesByLocation> carTypesByLocations = new ArrayList<>();

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

    public List<CarTypesByLocation> getCarTypesByLocations() {
        return carTypesByLocations;
    }
}
