package org.culpan.railops.dao;

import org.culpan.railops.model.Location;
import org.culpan.railops.model.Railroad;
import org.culpan.railops.model.Route;

import java.util.ArrayList;
import java.util.List;

public class LocationsDao extends BaseDao<Location> {
    private final static CarTypesByLocationDao carTypesByLocationDao = new CarTypesByLocationDao();
    private final static RailroadsDao railroadsDao = new RailroadsDao();

    public List<Location> loadAllForRailroad(Railroad r) {
        return executeListQuery(String.format("select l.id, l.name, l.staging from locations l " +
                "inner join location_railroads_xref  x on x.location_id = l.id " +
                "where x.railroad_id = %d", r.getId()), rs -> {
            List<Location> locations = new ArrayList<>();

            while (rs.next()) locations.add(itemFromResultSetRow(rs));

            return locations;
        });
    }

    public List<Location> loadAllForRoute(Route route) {
        return executeListQuery(String.format("select l.id, l.name, l.staging from locations l " +
                "inner join routes_locations_xref x on x.location_id = l.id " +
                "where x.route_id = %d", route.getId()), rs -> {
            List<Location> locations = new ArrayList<>();

            while (rs.next()) locations.add(itemFromResultSetRow(rs));

            return locations;
        });
    }

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
