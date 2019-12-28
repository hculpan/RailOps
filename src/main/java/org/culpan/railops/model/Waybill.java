package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class Waybill {
    private final List<WaybillStop> stops = new ArrayList<>();
    private String carId;

    public List<WaybillStop> getStops() {
        return stops;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
