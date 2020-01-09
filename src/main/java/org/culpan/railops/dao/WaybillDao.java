package org.culpan.railops.dao;

import org.culpan.railops.model.Location;
import org.culpan.railops.model.Waybill;
import org.culpan.railops.model.WaybillStop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WaybillDao extends BaseDao<Waybill> {
    @Override
    public boolean exists(Waybill item) {
        boolean result = false;

        if (item != null && !item.getCarId().isEmpty()) {
            String sql = "select count(*) as cnt from waybills " +
                    "where car_id = '" + item.getCarId() + "'";
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
    public List<Waybill> load() {
        return null;
    }

    @Override
    public boolean addOrUpdate(Waybill item, boolean autocommit) {
        return false;
    }

    @Override
    public void addOrUpdate(Waybill item) {

    }

    @Override
    public void delete(Waybill item) {

    }

    @Override
    public Waybill findById(int id) {
        return null;
    }

    @Override
    public Waybill find(Waybill item) {
        return findWaybill(item.getCarId());
    }

    @Override
    protected Waybill itemFromResultSetRow(ResultSet rs) throws SQLException {
        Waybill result = new Waybill();
    }

    public Waybill findWaybill(String carId) {
        return executeItemQuery("select * from waybills w " +
                "inner join locations l on w.location_id = l.id " +
                "where car_id = '" + carId + "';",
                rs -> {
                    Waybill result = null;

                    while ( rs.next() ) {
                        if (result == null) {
                            result = new Waybill();
                            result.setCarId(carId);
                        }

                        WaybillStop stop = new WaybillStop();
                        stop.setConsignee(rs.getString("consignee"));
                        stop.setRouting(rs.getString("routing"));
                        stop.setShipper_address(rs.getString("shipper_address"));
                        stop.setShipper(rs.getString("shipper"));
                        stop.setLading(rs.getString("lading"));
                        stop.setSequence(rs.getInt("waybill_sequence"));

                        Location l = new Location(rs.getString("name"));
                        l.setId(rs.getInt("location_id"));
                        l.setStaging(rs.getInt("staging") > 0);
                        stop.setLocation(l);

                        result.getStops().add(stop);
                    }

                    return result;
                });
    }
}
