package org.culpan.railops.dao;

import org.culpan.railops.model.Car;
import org.culpan.railops.model.Location;

import java.util.List;

public class CarsDao extends BaseDao<Car> {
    public boolean existsByRoadId(String roadId) {
        return (exists(Car.class, String.format("road_id = '%s'", roadId)));
    }

    public Car findByRoadId(String roadId) {
        List<Car> cars = find(String.format("road_id = '%s'", roadId));
        if (cars.size() != 1) return null;

        return cars.get(0);
    }

    public List<Car> loadCarsAtLocation(Location l) {
        return find(String.format("location_id = %d", l.getId()));
    }
}
