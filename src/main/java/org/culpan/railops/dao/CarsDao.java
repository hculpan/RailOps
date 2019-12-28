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

    public void setCarLocation(String carId, String locationName, int waybillSequence) {
        LocationsDao locationsDao = new LocationsDao();
        Location location = locationsDao.find(locationName);
        executeUpdate("update cars " +
                "set location_id = " + location.getId() +
                ", waybill_sequence = " + waybillSequence +
                " where id = '" + carId + "'");
    }

    @Override
    public void delete(Car c) {
        try {
            Waybill waybill = waybillDao.findWaybill(c.getId());
            if (waybill != null) {
                Datastore.instance.delete(waybill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        executeUpdate("delete from cars " +
                "where mark = '" + c.getMark() +
                "' and id = '" + c.getId() + "'");
    }

    @Override
    public Car find(Car item) {
        return find(item.getId());
    }

    @Override
    protected Car itemFromResultSetRow(ResultSet rs) throws SQLException {
        Car r = new Car(
                rs.getString("kind"),
                rs.getString("mark"),
                rs.getString("id"),
                rs.getString("aarCode"),
                rs.getString("description"),
                Datastore.instance.findLocationName(rs.getInt("location_id")));
        r.setSwitching(rs.getInt("switching") > 0);
        r.setWaybillSequence(rs.getInt("waybill_sequence"));
        Location l = locationsDao.find(rs.getInt("location_id"));
        if (l != null) {
            r.setLocation(l.getName());
        }
        r.setWaybill(Datastore.instance.carWaybillExists(r.getId()));
        return r;
    }

    @Override
    public List<Car> load() {
        return executeListQuery("select * from cars order by mark, id", rs -> {
            List<Car> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }
    
    @Override
    public boolean exists(Car c) {
        boolean result = false;

        if (c != null && c.getId() != null && !c.getId().isEmpty()) {
            String sql = "select count(*) as cnt from cars " +
                    "where mark = '" + c.getMark() + "' and id = '" + c.getId() + "'";
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
    public void addOrUpdate(Car c) {
        if (exists(c)) {
            Integer locationId = null;
            if (c.getLocation() != null && !c.getLocation().isEmpty()) {
                LocationsDao locationsDao = new LocationsDao();
                Location l = locationsDao.find(c.getLocation());
                locationId = (l != null ? l.getId() : null);
            }

            executeUpdate("update cars " +
                    "set kind = '" + c.getKind() +
                    "', aarCode = '" + c.getAarCode() +
                    "', description = '" + c.getDescription() +
                    "', location_id = " + (locationId != null ? locationId.intValue() : null)  +
                    " , waybill_sequence = " + c.getWaybillSequence() +
                    " , switching = " + c.getSwitchingValue() + " " +
                    "where mark = '" + c.getMark() +
                    "' and id = '" + c.getId() + "'");
        } else {
            executeUpdate("insert into cars (kind, mark, id, aarCode, description, waybill_sequence, switching) " +
                    "values ('" + c.getKind() + "','" + c.getMark() + "','" +
                    c.getId() + "','" + c.getAarCode() + "','" + c.getDescription() + "', -1, " +
                    c.getSwitchingValue() + ")");
        }
    }

    public Car find(String id) {
        Object o = executeItemQuery("select * from cars " +
                "where id = '" + id + "'", rs -> {
            if ( rs.next() ) {
                return itemFromResultSetRow(rs);
            }
            return null;
        });
        return (Car)o;
    }

    public void moveCarTo(String carId, int locationId, boolean nextWaybill) {
        Car c = find(carId);
        if (c != null) {
            c.setSwitching(false);
            c.setLocation(locationsDao.find(locationId).getName());

            if (nextWaybill) {
                int newWaybillSequence = c.getWaybillSequence();

                Waybill waybill = waybillDao.findWaybill(carId);
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

    public void updateSwitching(String id, boolean value) {
        executeUpdate("update cars set switching = " + (value ? 1 : 0) +
                " where id = '" + id + "'");
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
