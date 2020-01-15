package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostAddOrUpdate;
import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Route;

public class RoutesDao extends BaseDao<Route> implements PostInitialize<Route>, PostAddOrUpdate<Route> {
    private final static LocationsDao locationsDao = new LocationsDao();

    @Override
    public void initializeComplete(Route item) {
        item.getLocations().clear();
        item.getLocations().addAll(locationsDao.loadAllForRoute(item));
    }

    @Override
    public void addOrUpdateComplete(Route item, boolean autocommit) {
        executeUpdate(String.format("delete from routes_locations_xref where route_id = %d", item.getId()), autocommit);
        for (Location l : item.getLocations()) {
            executeUpdate(String.format("insert into routes_locations_xref " +
                    "(route_id, location_id) " +
                    "values(%d, %d)",
                    item.getId(),
                    l.getId()),
                    autocommit);
        }
    }

}
