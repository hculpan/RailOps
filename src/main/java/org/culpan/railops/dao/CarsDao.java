package org.culpan.railops.dao;

import org.culpan.railops.model.Car;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Waybill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarsDao extends BaseDao<Car> {
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static WaybillDao waybillDao = new WaybillDao();

    public void setCarLocation(String carId, String locationName) {
        setCarLocation(carId, locationName, -1);
    }

    public void setCarLocation(String carRoadId, String locationName, int waybillSequence) {
        LocationsDao locationsDao = new LocationsDao();
        Location location = locationsDao.find(locationName);
        executeUpdate("update cars " +
                "set location_id = " + location.getId() +
                ", waybill_sequence = " + waybillSequence +
                " where road_id = '" + carRoadId + "'");
    }

    @Override
    public void delete(Car c) {
        Waybill waybill = waybillDao.findWaybill(c.getRoadId());
        if (waybill != null) {
            waybillDao.delete(waybill);
        }

        executeUpdate("delete from cars where id = " + c.getId());
    }

    @Override
    public Car findById(int id) {
        return executeItemQuery("select * from cars where id = " + id,
                rs -> {
                    if (rs.next()) return itemFromResultSetRow(rs);
                    return null;
                });
    }

    @Override
    protected Car itemFromResultSetRow(ResultSet rs) throws SQLException {
        Car r = new Car();
        r.setId(rs.getInt("id"));
        r.setKind(rs.getString("kind"));
        r.setRoadId(rs.getString("road_id"));
        r.setAarCode(rs.getString("aarCode"));
        r.setDescription(rs.getString("description"));
        r.setLocationId(rs.getInt("location_id"));
        r.setSwitching(rs.getInt("switching") > 0);
        r.setWaybillSequence(rs.getInt("waybill_sequence"));
        r.setWaybillId(rs.getInt("waybill_id"));
        return r;
    }

    @Override
    public List<Car> load() {
        return executeListQuery("select * from cars", rs -> {
            List<Car> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    @Override
    public boolean exists(Car c) {
        return findById(c.getId()) != null;
    }

    @Override
    public boolean addOrUpdate(Car c, boolean autocommit) {
        if (exists(c)) {
            return executeUpdate(String.format("update cars " +
                    "set kind = '%s', " +
                    "    road_id = '%s', " +
                    "    aarCode = '%s', " +
                    "    description = '%s', " +
                    "    location_id = %d, " +
                    "    waybill_sequence = %d, " +
                    "    waybill_id = %d, " +
                    "    switching = %d " +
                    "where id = %d",
                    c.getKind(),
                    c.getRoadId(),
                    c.getAarCode(),
                    c.getDescription(),
                    c.getLocationId(),
                    c.getWaybillSequence(),
                    c.getWaybillId(),
                    (c.isSwitching() ? 1 : 0)), autocommit);
        } else {
            if (executeUpdate(String.format("insert into cars " +
                    "(kind, road_id, aarCode, description, location_id, waybill_sequence, waybill_id, switching) " +
                    "values ('%s','%s','%s','%s',%d,%d,%d,%d)",
                    c.getKind(),
                    c.getRoadId(),
                    c.getAarCode(),
                    c.getDescription(),
                    c.getLocationId(),
                    c.getWaybillSequence(),
                    c.getWaybillId(),
                    (c.isSwitching() ? 1 : 0), autocommit))) {
                c.setId(getLastInsertId());
                return true;
            } else {
                return false;
            }
        }
    }

    public Car findByRoadId(String id) {
        return executeItemQuery("select * from cars " +
                "where road_id = '" + id + "'", rs -> {
            if ( rs.next() ) {
                return itemFromResultSetRow(rs);
            }
            return null;
        });
    }

    public void moveCarTo(String carRoadId, int locationId, boolean nextWaybill) {
        Car c = findByRoadId(carRoadId);
        if (c != null) {
            c.setSwitching(false);
            c.setLocationId(locationId);

            if (nextWaybill) {
                int newWaybillSequence = c.getWaybillSequence();

                Waybill waybill = waybillDao.findWaybill(carRoadId);
                if (newWaybillSequence == waybill.getStops().size() - 1) {
                    newWaybillSequence = 0;
                } else {
                    newWaybillSequence++;
                }
                c.setWaybillSequence(newWaybillSequence);
            }
            addOrUpdate(c);
        }
    }

    public boolean updateSwitching(String roadId, boolean value) {
        return executeUpdate("update cars set switching = " + (value ? 1 : 0) +
                " where road_id = '" + roadId + "'");
    }

    public List<Car> carsAtLocation(Location l) {
        return executeListQuery("select * from cars where location_id = " + l.getId(),
                rs -> {
                    List<Car> result = new ArrayList<>();
                    while ( rs.next() ) {
                        result.add(itemFromResultSetRow(rs));
                    }
                    return result;
                });
    }
}
