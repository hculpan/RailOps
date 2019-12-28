package org.culpan.railops.dao;

import org.culpan.railops.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationsDao extends BaseDao<Location> {
    public Location find(String name) {
        Object o = executeItemQuery("select * from locations " +
                "where name = '" + name + "'", rs -> {
            if ( rs.next() ) {
                return itemFromResultSetRow(rs);
            }
            return null;
        });
        return (Location)o;
    }

    public Location find(int id) {
        Object o = executeItemQuery("select * from locations " +
                "where id = " + id, rs -> {
            if ( rs.next() ) {
                return itemFromResultSetRow(rs);
            }
            return null;
        });
        return (Location)o;
    }

    public boolean exists(String location) {
        return find(location) != null;
    }

    @Override
    public boolean exists(Location item) {
        boolean result = false;

        if (item != null && item.getId() > -1) {
            String sql = "select count(*) as cnt from locations " +
                    "where id = " + item.getId();
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
    public List<Location> load() {
        return executeListQuery("select * from locations order by name", rs -> {
            List<Location> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    @Override
    public void addOrUpdate(Location item) {
        if (exists(item)) {
            executeUpdate("update locations " +
                    "set name = '" + item.getName() +
                    "', staging = " + item.isStaging() +
                    "where id = " + item.getId());
        } else {
            executeUpdate("insert into locations (name, staging) " +
                    "values ('" + item.getName() + "'," + (item.isStaging() ? 1 : 0) + ")");
        }
    }

    @Override
    public void delete(Location item) {
        if (exists(item)) {
            executeUpdate("delete from location_cars_xref " +
                    "where location_id = " + item.getId(), false);

            executeUpdate("delete from location_railroads_xref " +
                    "where location_id = " + item.getId(), false);

            executeUpdate("delete from locations " +
                    "where id = " + item.getId(), false);

            commit();
        }
    }

    @Override
    public Location find(Location item) {
        return find(item.getId());
    }

    @Override
    protected Location itemFromResultSetRow(ResultSet rs) throws SQLException {
        Location result = new Location(rs.getInt("id"), rs.getString("name"));
        result.setStaging(rs.getInt("staging") > 0);
        result.getCarsTypes().addAll(loadLocationCars(result.getName()));
        result.getRailroads().addAll(loadLocationRailroads(result.getName()));
        return result;
    }

    public List<Location> allLocationsForRailroad(String railroadMark) {
        return executeListQuery("select * from locations l " +
                "inner join location_railroads_xref lrx on l.id = lrx.location_id " +
                "where lrx.reportingMark = '" + railroadMark + "'", rs -> {
            List<Location> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    public boolean doesRailroadServeLocation(String railroad, Location l) {
        Object o = executeItemQuery("select count(*) as cnt from location_railroads_xref " +
                "where location_id = " + l.getId() + " and reportingMark = '" + railroad + "'",
                rs -> {
                    if (rs.next()) {
                        return (rs.getInt("cnt") > 0);
                    }
                    return false;
                }
        );
        return (Boolean)o;
    }
}
