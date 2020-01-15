package org.culpan.railops.dao;

import org.culpan.railops.model.Location;

import java.util.List;

public class LocationsDao extends BaseDao<Location> {
    private final static CarTypesByLocationDao carTypesByLocationDao = new CarTypesByLocationDao();
    private final static RailroadsDao railroadsDao = new RailroadsDao();

    @Override
    public boolean delete(Location location, boolean autocommit) {
        carTypesByLocationDao.deleteAllForLocation(location, autocommit);
        railroadsDao.deleteAllRailroadForLocation(location, autocommit);
        return super.delete(location, autocommit);
    }

    public Location findByName(String name) {
        if (name == null || name.isBlank()) return null;

        List<Location> locations = find(String.format("name = '%s'", name));
        return (locations.size() > 0 ? locations.get(0) : null);
    }
}
