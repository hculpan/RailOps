package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.Table;

@Table(name = "car_types_by_location")
public class CarTypesByLocation extends BaseModel {
    @Column(name = "location_id")
    private int locationId;

    @Column
    private String aarCode;

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getAarCode() {
        return aarCode;
    }

    public void setAarCode(String aarCode) {
        this.aarCode = aarCode;
    }
}
