package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class Waybill extends BaseModel {
    private final List<WaybillStop> stops = new ArrayList<>();
    private int carId;

    public List<WaybillStop> getStops() {
        return stops;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }
}
