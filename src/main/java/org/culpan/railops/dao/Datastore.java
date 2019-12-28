package org.culpan.railops.dao;

import org.culpan.railops.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datastore {
    public static final Datastore instance = new Datastore();

    private static Connection conn;

    public Connection connection() {
        if (instance.conn == null) {
            try {
                initDb("/Users/harryculpan/.railops/railops.sqlite");
            } catch (ClassNotFoundException | SQLException e) {
                logDbError(e);
            }
        }

        return instance.conn;
    }

    protected boolean executeUpdate(String sql, boolean commit) {
        try {
            int rowCount;
            Statement stmt = connection().createStatement();
            rowCount = stmt.executeUpdate(sql);
            stmt.close();
            if (commit) connection().commit();
            return rowCount > 0;
        } catch (SQLException e) {
            logDbError(e);

            return false;
        }
    }

    protected boolean executeUpdate(String sql) {
        return executeUpdate(sql, true);
    }

    public void logDbError(Throwable t) {
        t.printStackTrace();
    }

    protected void initDb(String dbPath) throws ClassNotFoundException, SQLException {
        conn = null;

        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        conn.setAutoCommit(false);
    }

    public void closeDb() throws SQLException {
        if (conn != null) {
            conn.close();;
            conn = null;
        }
    }

    public List<Railroad> loadRailroads() throws SQLException {
        List<Railroad> result = new ArrayList<>();

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select * from railroads order by reportingMark;" );

        while ( rs.next() ) {
            Railroad r = new Railroad(rs.getString("reportingMark"), rs.getString("name"));
            result.add(r);
        }
        rs.close();
        stmt.close();

        return result;
    }

    public List<String> loadRailroadMarks() throws SQLException {
        List<String> result = new ArrayList<>();

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select reportingMark from railroads order by reportingMark;" );

        while ( rs.next() ) {
            result.add(rs.getString("reportingMark"));
        }
        rs.close();
        stmt.close();

        return result;
    }

    public List<String> loadLocationCars(String locationName) throws SQLException {
        List<String> result = new ArrayList<>();

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select * from location_cars_xref lcx " +
                "inner join locations l on l.id = lcx.location_id " +
                "where name = '" + locationName +
                "' order by aarCode;");

        while ( rs.next() ) {
            result.add(rs.getString("aarCode"));
        }
        rs.close();
        stmt.close();

        return result;
    }

    public List<String> loadLocationRailroads(String locationName) throws SQLException {
        List<String> result = new ArrayList<>();

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select * from location_railroads_xref lrx " +
                "inner join locations l on l.id = lrx.location_id " +
                "where name = '" + locationName +
                "' order by reportingMark;");

        while ( rs.next() ) {
            result.add(rs.getString("reportingMark"));
        }
        rs.close();
        stmt.close();

        return result;
    }

    public boolean doesRailroadMarkExist(String mark) throws SQLException {
        boolean result = false;

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select count(*) as cnt from railroads where reportingMark = '" + mark + "';" );

        if ( rs.next() ) {
            if (rs.getInt("cnt") > 0) result = true;
        }
        rs.close();
        stmt.close();

        return result;
    }

    public void addOrUpdate(Railroad r) throws SQLException {
        if (doesRailroadMarkExist(r.getReportingMark())) {
            Statement stmt = connection().createStatement();
            String sql = "update railroads " +
                    "set name = '" + r.getName() + "' " +
                    "where reportingMark = '" + r.getReportingMark() + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            connection().commit();
        } else {
            Statement stmt = connection().createStatement();
            String sql = "insert into railroads (reportingMark, name) " +
                    "values ('" + r.getReportingMark() + "','" + r.getName() + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            connection().commit();
        }
    }

    public String findLocationName(int id) {
        String result = null;

        try {
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery("select * from locations " +
                    "where id = " + id);

            if (rs.next()) {
                result = rs.getString("name");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean carWaybillExists(String carId) {
        boolean result = false;

        try {
            Statement stmt = Datastore.instance.connection().createStatement();
            ResultSet rs = stmt.executeQuery("select count(*) as cnt from waybills " +
                    "where car_id = '" + carId + "';");

            if (rs.next()) {
                if (rs.getInt("cnt") > 0) result = true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    public void addOrUpdate(Waybill waybill) throws SQLException {
        if (carWaybillExists(waybill.getCarId())) {
            delete(waybill);
        }

        for (WaybillStop stop : waybill.getStops()) {
            LocationsDao locationsDao = new LocationsDao();
            Location l = locationsDao.find(stop.getDestination());
            Statement stmt = connection().createStatement();
            String sql = "insert into waybills "+
                    "(consignee, location_id, routing, shipper_address, lading, shipper, waybill_sequence, car_id)  " +
                    "values ('" + stop.getConsignee() +
                    "','" + (l != null ? l.getId() : null) +
                    "','" + stop.getRouting() +
                    "','" + stop.getShipper_address() +
                    "','" + stop.getLading() +
                    "','" + stop.getShipper() +
                    "'," + stop.getSequence() +
                    ",'" + waybill.getCarId() +
                    "');";
            stmt.executeUpdate(sql);
            stmt.close();
        }

        connection().commit();
    }

    public void delete(Waybill waybill) throws SQLException {
        Statement stmt = connection().createStatement();
        String sql = "delete from waybills " +
                "where car_id = '" + waybill.getCarId() + "'";
        stmt.executeUpdate(sql);
        stmt.close();
        connection().commit();
    }

    public void delete(Railroad r) throws SQLException {
        Statement stmt = connection().createStatement();
        String sql = "delete from railroads " +
                "where reportingMark = '" + r.getReportingMark() + "'";
        stmt.executeUpdate(sql);
        stmt.close();
        connection().commit();
    }

    public boolean existsLocationCarType(String location, String aarCode) throws SQLException {
        boolean result = false;

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select count(*) as cnt from location_cars_xref lcx " +
                "inner join locations l on l.id = lcx.location_id " +
                "where name = '" + location +
                "' and aarCode = '" + aarCode +"';");

        if ( rs.next() ) {
            if (rs.getInt("cnt") > 0) result = true;
        }
        rs.close();
        stmt.close();

        return result;
    }

    public boolean existsLocationRailroad(String location, String mark) throws SQLException {
        boolean result = false;

        Statement stmt = connection().createStatement();
        ResultSet rs = stmt.executeQuery( "select count(*) as cnt from location_railroads_xref lrx " +
                "inner join locations l on l.id = lrx.location_id " +
                "where name = '" + location +
                "' and reportingMark = '" + mark + "';");

        if ( rs.next() ) {
            if (rs.getInt("cnt") > 0) result = true;
        }
        rs.close();
        stmt.close();

        return result;
    }

    public void addLocationCarType(String location, String aarCode) throws SQLException {
        if (!existsLocationCarType(location, aarCode)) {
            LocationsDao locationsDao = new LocationsDao();
            Location location1 = locationsDao.find(location);
            if (location1 != null) {
                Statement stmt = connection().createStatement();
                String sql = "insert into location_cars_xref (location_id, aarCode) " +
                        "values(" + location1.getId() + ",'" + aarCode + "')";
                stmt.executeUpdate(sql);
                stmt.close();
                connection().commit();
            }
        }
    }

    public void addLocationRailroad(String location, String railroad) throws SQLException {
        if (!existsLocationRailroad(location, railroad)) {
            LocationsDao locationsDao = new LocationsDao();
            Location location1 = locationsDao.find(location);
            if (location1 != null) {
                Statement stmt = connection().createStatement();
                String sql = "insert into location_railroads_xref (location_id, reportingMark) " +
                        "values(" + location1.getId() + ",'" + railroad + "')";
                stmt.executeUpdate(sql);
                stmt.close();
                connection().commit();
            }
        }
    }

    public void deleteLocationCarType(String location, String aarCode) throws SQLException {
        LocationsDao locationsDao = new LocationsDao();
        Location location1 = locationsDao.find(location);

        if (location1 != null) {
            Statement stmt = connection().createStatement();
            String sql = "delete from location_cars_xref " +
                    "where location_id = " + location1.getId() +
                    "  and aarCode = '" + aarCode + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            connection().commit();
        }
    }

    public void deleteLocationRailroad(String location, String reportingMark) throws SQLException {
        LocationsDao locationsDao = new LocationsDao();
        Location location1 = locationsDao.find(location);

        if (location1 != null) {
            Statement stmt = connection().createStatement();
            String sql = "delete from location_railroads_xref " +
                    "where location_id = " + location1.getId() +
                    "  and reportingMark = '" + reportingMark + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            connection().commit();
        }
    }

}
