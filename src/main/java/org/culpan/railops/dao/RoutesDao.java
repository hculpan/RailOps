package org.culpan.railops.dao;

import org.culpan.railops.model.Location;
import org.culpan.railops.model.Route;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoutesDao extends BaseDao<Route> {
    private LocationsDao locationsDao = new LocationsDao();

    @Override
    public boolean exists(Route route) {
        boolean result = false;

        if (route != null && route.getId() > 0) {
            String sql = "select count(*) as cnt from routes " +
                    "where id = " + route.getId();
            Object o = executeItemQuery(sql, rs -> {
                if (rs.next()) {
                    return (rs.getInt("cnt") > 0);
                }
                return false;
            });
            result = ((Boolean)o).booleanValue();
        }

        return result;
    }

    @Override
    public void addOrUpdate(Route route) {
        if (exists(route)) {
            executeUpdate("update routes " +
                    "set name = '" + route.getName() +
                    "', railroad = '" + route.getRailroad() +
                    "', switch_list_id = " + route.getSwitchListId() +
                    " where id = " + route.getId(), false);

            executeUpdate("delete from routes_locations_xref " +
                    "where route_id = " + route.getId(), false);

            commit();
        } else {
            executeUpdate("insert into routes (name, railroad, switch_list_id) " +
                    "values('" + route.getName() + "','" +
                    route.getRailroad() + "', " +
                    route.getSwitchListId() + ")");
        }

        if (route.getId() < 1) {
            route.setId((int)executeQuery("select id from routes where name = '" + route.getName() + "'",
                    rs -> {
                        if (rs.next()) {
                            return rs.getInt("id");
                        }
                        return 0;
                    }));
        }

        for (Location l : route.getStops()) {
            executeUpdate("insert into routes_locations_xref " +
                            "(route_id, location_id) " +
                            "values(" + route.getId() + "," + l.getId() + ")"
                    , false);
        }

        commit();
    }

    protected void deleteStops(int id) {
        executeUpdate("delete from routes_locations_xref " +
                "where route_id = " + id);
    }

    @Override
    public void delete(Route item) {
        deleteStops(item.getId());

        executeUpdate("delete from routes where id = " + item.getId());
    }

    @Override
    public Route find(Route item) {
        return find(item.getId());
    }

    protected Route itemFromResultSetRow(ResultSet rs) throws SQLException {
        Route route = new Route(rs.getString("name"), rs.getString("railroad"));
        route.setId(rs.getInt("id"));
        route.setSwitchListId(rs.getInt("switch_list_id"));
        route.getStops().addAll(getStops(route.getId()));
        return route;
    }

    public Route find(String name) {
        Route result = executeItemQuery("select * from routes where name = '" + name + "'",
                rs -> {
                    if (rs.next()) {
                        return itemFromResultSetRow(rs);
                    }
                    return null;
                });

        if (result != null) {
            result.getStops().clear();
            result.getStops().addAll(getStops(result.getId()));
        }

        return result;
    }

    public Route find(int id) {
        Route result = executeItemQuery("select * from routes where id = " + id,
                rs -> {
                    if (rs.next()) {
                        return itemFromResultSetRow(rs);
                    }
                    return null;
                });

        if (result != null) {
            result.getStops().clear();
            result.getStops().addAll(getStops(result.getId()));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected List<Location> getStops(int id) {
        List<Location> result = new ArrayList<>();

        List<Integer> locIds = (List<Integer>)executeQuery("select location_id from routes_locations_xref " +
                        "where route_id = " + id + " " +
                        "order by routes_locations_xref_id",
                rs -> {
                    List<Integer> resultList = new ArrayList<>();
                    while (rs.next()) {
                        resultList.add(rs.getInt("location_id"));
                    }
                    return resultList;
                }
        );

        for (Integer i : locIds) {
            Location l = locationsDao.find(i);
            if (l != null) {
                result.add(l);
            }
        }

        return result;
    }

    @Override
    public List<Route> load() {
        return executeListQuery("select * from routes order by name", rs -> {
            List<Route> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }
}
