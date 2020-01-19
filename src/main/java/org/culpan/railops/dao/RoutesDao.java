package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostAddOrUpdate;
import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Route;

import java.util.List;

public class RoutesDao extends BaseDao<Route> implements PostInitialize<Route>, PostAddOrUpdate<Route> {
    private final static LocationsDao locationsDao = new LocationsDao();

    @Override
    public void initializeComplete(Route item) {
        item.getLocations().clear();
        item.getLocations().addAll(locationsDao.loadAllForRoute(item));
    }

    public Route findByNameAndIdentifier(String name, String identifier) {
        List<Route> routes = find(String.format("name = '%s' and identifier = '%s'",
                name, identifier));
        if (routes.size() != 1) return null;
        return routes.get(0);
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

    public Route findByNameAndRailroadName(String railroadName, String identifier) {
        return executeItemQuery(String.format("select r.* " +
                "from routes r " +
                "inner join railroads rr on rr.id = r.railroad_id " +
                "where rr.name = '%s' or rr.short_name = '%s'" +
                "  and identifier = '%s'", railroadName, railroadName, identifier), rs -> {
            if (rs.next()) return itemFromResultSetRow(rs);
            else return null;
        });
    }

}
