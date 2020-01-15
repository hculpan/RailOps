package org.culpan.railops.dao;

import org.culpan.railops.model.Location;
import org.culpan.railops.model.Railroad;

import java.util.ArrayList;
import java.util.List;

public class RailroadsDao extends BaseDao<Railroad> {
    public List<Railroad> loadRailroadsByLocation(Location l) {
        String sql = "select * from railroads r " +
                "inner join location_railroads_xref x on x.railroad_id = r.id " +
                "where x.location_id = %d ";
        return executeListQuery(String.format(sql, l.getId()),
                rs-> {
                    List<Railroad> railroads = new ArrayList<>();
                    while (rs.next()) railroads.add(itemFromResultSetRow(rs));
                    return railroads;
                });
    }

    public boolean existsForLocation(Railroad r, Location l) {
        return (Boolean)executeQuery(String.format("select id " +
                "from location_railroads_xref " +
                "where location_id = %d " +
                "  and railroad_id = %d",
                l.getId(),
                r.getId()),
                rs -> rs.next());
    }

    public boolean addRailroadForLocation(Railroad r, Location l) {
        if (r == null || r.getId() <= 0 || l == null || l.getId() <= 0) return false;
        if (existsForLocation(r, l)) return true;

        return executeUpdate(String.format("insert into location_railroads_xref " +
                "(railroad_id, location_id)" +
                "values (%d, %d)", r.getId(), l.getId()));
    }

    public boolean deleteRailroadForlocation(Railroad r, Location l) {
        return deleteRailroadForlocation(r, l, true);
    }

    public boolean deleteRailroadForlocation(Railroad r, Location l, boolean autocommit) {
        return executeUpdate(String.format("delete from location_railroads_xref " +
                "where railroad_id = %d " +
                "  and location_id = %d",
                r.getId(),
                l.getId()),
                autocommit);
    }

    public boolean deleteAllRailroadForLocation(Location l, boolean autocommit) {
        return executeUpdate(String.format("delete from location_railroads_xref " +
                        "where location_id = %d",
                l.getId()),
                autocommit);
    }

    public Railroad findByMark(String mark) {
        List<Railroad> railroads = find(String.format("mark = '%s'", mark));
        if (railroads.size() != 1) return null;
        return railroads.get(0);
    }
}
