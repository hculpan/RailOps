package org.culpan.railops.dao;

import org.culpan.railops.model.Move;
import org.culpan.railops.model.SwitchList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class  SwitchListDao extends BaseDao<SwitchList> {
    private final static CarsDao carsDao = new CarsDao();

    @Override
    public boolean exists(SwitchList item) {
        if (item != null && item.getId() > 0) {
            return existsHelper("switch_lists", item.getId());
        } else {
            return false;
        }
    }

    @Override
    public List<SwitchList> load() {
        return executeListQuery("select * from switch_lists order by id", rs -> {
            List<SwitchList> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    protected void updateMoves(SwitchList item) {
        deleteMoves(item);
        for (Move m : item.getMoves()) {
            executeUpdate("insert into moves (car_id, move, location_id, switch_list_id, lading) " +
                    "values('" + m.getCar().getId() +
                    "', '" + m.getMove() +
                    "', " + m.getLocationId() +
                    " , " + item.getId() +
                    " , '" + m.getLading() +
                    "')", false);
        }
        commit();
    }

    @Override
    public boolean addOrUpdate(SwitchList item, boolean autocommit) {
        if (exists(item)) {
            updateMoves(item);
            return executeUpdate("update switch_lists " +
                    "set route_id = " + item.getRouteId() +
                    ", status = '" + item.getStatus() +
                    "' where id = " + item.getId());
        } else {
            if (executeUpdate("insert into switch_lists (route_id, status) " +
                    "values(" + item.getRouteId() + ",'" + item.getStatus() + "')")) {
                item.setId(getLastInsertId());
                updateMoves(item);
                return true;
            } else {
                return false;
            }
        }
    }

    public void deleteMoves(SwitchList item) {
        if (item.getId() > 0) {
            executeUpdate("delete from moves where switch_list_id = " + item.getId());
        }
    }

    @Override
    public void delete(SwitchList item) {
        deleteMoves(item);
        executeUpdate("delete from switch_lists where id = " + item.getId());
    }

    @Override
    public SwitchList findById(int id) {
        return null;
    }

    @Override
    public SwitchList find(SwitchList item) {
        return findHelper("switch_lists", item.getId());
    }

    public SwitchList find(int id) {
        return findHelper("switch_lists", id);
    }

    @SuppressWarnings("unchecked")
    protected List<Move> loadMoves(int switchListId) {
        Object o = executeQuery("select * from moves " +
                "where switch_list_id = " + switchListId +
                " order by id", rs -> {
            List<Move> result = new ArrayList<>();

            while (rs.next()) {
                result.add(moveFromResultSetRow(rs));
            }

            return result;
        });
        return (List<Move>)o;
    }

    protected Move moveFromResultSetRow(ResultSet rs) throws SQLException {
        Move m = new Move();
        m.setId(rs.getInt("id"));
        m.setCar(carsDao.findById(rs.getInt("car_id")));
        m.setLocationId(rs.getInt("location_id"));
        m.setMove(rs.getString("move"));
        m.setLading(rs.getString("lading"));
        return m;
    }

    @Override
    protected SwitchList itemFromResultSetRow(ResultSet rs) throws SQLException {
        SwitchList switchList = new SwitchList();

        switchList.setId(rs.getInt("id"));
        switchList.setRouteId(rs.getInt("route_id"));
        switchList.setStatus(rs.getString("status"));
        switchList.getMoves().clear();
        switchList.getMoves().addAll(loadMoves(switchList.getId()));

        return switchList;
    }
}
