package org.culpan.railops.model;

import org.culpan.railops.dao.WaybillStopsDao;
import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToMany;
import org.culpan.railops.dao.annotations.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "waybills")
public class Waybill extends BaseModel {
    @OneToMany(foreignKeyName = "waybill_id", keyFieldName = "waybillId", dao = WaybillStopsDao.class, persist = true)
    private final List<WaybillStop> stops = new ArrayList<>();

    @Column(name = "car_id")
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
