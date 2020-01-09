package org.culpan.railops.dao;

import org.culpan.railops.model.Railroad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RailroadDao extends BaseDao<Railroad> {
    @Override
    public boolean exists(Railroad item) {
        return findById(item.getId()) != null;
    }

    @Override
    public List<Railroad> load() {
        return executeListQuery("select * from railroads ",
                rs -> {
                    List<Railroad> r = new ArrayList<>();

                    while (rs.next()) r.add(itemFromResultSetRow(rs));

                    return r;
                });
    }

    @Override
    public boolean addOrUpdate(Railroad item, boolean autocommit) {
        if (exists(item)) {
            return executeUpdate(String.format("update railroads " +
                    "set mark = '%s', name = '%s' " +
                    "where id = %d",
                    item.getMark(),
                    item.getName(),
                    item.getId()), autocommit);
        } else {
            if (executeUpdate(String.format("insert into railroads " +
                            "(mark, name) " +
                            "values ('%s', '%s')",
                    item.getMark(),
                    item.getName()), autocommit)) {
                item.setId(getLastInsertId());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void delete(Railroad item) {

    }

    @Override
    public Railroad findById(int id) {
        return executeItemQuery("select * from railroads where id = " + id,
                rs -> {
                    if (rs.next()) return itemFromResultSetRow(rs);
                    return null;
                });
    }

    @Override
    protected Railroad itemFromResultSetRow(ResultSet rs) throws SQLException {
        Railroad railroad = new Railroad(rs.getString("mark"), rs.getString("name"));
        railroad.setId(rs.getInt("id"));
        return railroad;
    }
}
