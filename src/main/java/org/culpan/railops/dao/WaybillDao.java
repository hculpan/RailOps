package org.culpan.railops.dao;

import org.culpan.railops.model.Car;
import org.culpan.railops.model.Waybill;

import java.util.List;

public class WaybillDao extends BaseDao<Waybill> {
    private final static CarsDao carsDao = new CarsDao();

    public Waybill findByRoadId(String roadId) {
        if (!existsForCarRoadId(roadId)) return null;

        Car car = carsDao.findByRoadId(roadId);
        if (car == null) return null;

        List<Waybill> waybills = find(String.format("car_id = %d", car.getId()));
        if (waybills.size() != 1) return null;
        else return waybills.get(0);
    }

    public boolean existsForCarRoadId(String roadId) {
        Car car = carsDao.findByRoadId(roadId);
        if (car == null) return false;

        return exists(Waybill.class, String.format("car_id = %d", car.getId()));
    }
}
