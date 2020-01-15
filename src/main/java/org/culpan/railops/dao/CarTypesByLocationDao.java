package org.culpan.railops.dao;

import org.culpan.railops.model.CarTypesByLocation;
import org.culpan.railops.model.Location;

public class CarTypesByLocationDao extends BaseDao<CarTypesByLocation> {
    public boolean existsForLocation(Location location, String aarCode) {
        return exists(CarTypesByLocation.class,
                String.format("aarCode = '%s' and location_id = %d", aarCode, location.getId()));
    }

    public boolean deleteAllForLocation(Location location) {
        return deleteAllForLocation(location, true);
    }

    public boolean deleteAllForLocation(Location location, boolean autocommit) {
        return executeUpdate(
                String.format("delete from car_types_by_location where location_id = %d", location.getId()), autocommit);
    }

    public boolean deleteForLocation(Location location, String aarCode) {
        return deleteForLocation(location, aarCode, true);
    }

    public boolean deleteForLocation(Location location, String aarCode, boolean autocommit) {
        return executeUpdate(
                String.format("delete from car_types_by_location where aarCode = '%s' and location_id = %d",
                        aarCode,
                        location.getId()),
                autocommit);
    }
}
