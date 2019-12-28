package org.culpan.railops.dao;

import org.culpan.railops.model.Move;
import org.culpan.railops.model.Route;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoveDao extends BaseDao<Move> {
    private LocationsDao locationsDao = new LocationsDao();
    private RoutesDao routesDao = new RoutesDao();
    private CarsDao carsDao = new CarsDao();

    @Override
    public boolean exists(Move item) {
        boolean result = false;

        if (item != null && item.getId() > 0) {
            String sql = "select count(*) as cnt from moves " +
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

    public List<Move> loadForRoute(int routeId) {
        return executeListQuery("select * from moves " +
                "where route_id = " + routeId +
                " order by id", rs -> {
            List<Move> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    @Override
    public List<Move> load() {
        return executeListQuery("select * from moves order by route_id, id", rs -> {
            List<Move> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    @Override
    public void addOrUpdate(Move item) {
        if (exists(item)) {
            String sql = "update moves set route_id = ";
            Route route = routesDao.find(item.getRouteName());
            sql += Integer.toString(route.getId()) + ", car_id = '" + item.getCarId() +
                    "', move = '" + item.getMove() + "'";
            executeUpdate(sql);
        } else {
            String sql = "insert into moves (route_id, car_id, move) values (";
            Route route = routesDao.find(item.getRouteName());
            sql += Integer.toString(route.getId()) + ",'" + item.getCarId() + "','" + item.getMove() + "'";
            executeUpdate(sql);
        }
    }

    @Override
    public void delete(Move item) {
        executeUpdate("delete from moves where id = " + item.getId());
    }

    @Override
    public Move find(Move item) {
        return findHelper("moves", item.getId());
    }

    @Override
    protected Move itemFromResultSetRow(ResultSet rs) throws SQLException {
        Move m = new Move();
        m.setId(rs.getInt("id"));
        m.setRouteName(routesDao.find(rs.getInt("route_id")).getName());
        m.setCarId(rs.getString("car_id"));
        m.setMove(rs.getString("move"));
        return m;
    }
}
