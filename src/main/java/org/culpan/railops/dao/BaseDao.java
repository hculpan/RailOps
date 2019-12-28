package org.culpan.railops.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class BaseDao<T> extends Datastore {
    public interface QueryResultsInterface {
        Object evaluateResults(ResultSet set) throws SQLException;
    }

    public abstract boolean exists(T item);

    public abstract List<T> load();

    public abstract void addOrUpdate(T item);

    public abstract void delete(T item);

    public abstract T find(T item);

    protected abstract T itemFromResultSetRow(ResultSet rs) throws SQLException;

    protected boolean existsHelper(String tableName, int id) {
        boolean result = false;

        String sql = "select count(*) as cnt from " + tableName +
                " where id = " + id;
        Object o = executeItemQuery(sql, rs -> {
            if (rs.next()) {
                return (rs.getInt("cnt") > 0);
            }
            return false;
        });
        result = ((Boolean)o).booleanValue();

        return result;
    }

    protected T findHelper(String tableName, int id) {
        return executeItemQuery("select * from " + tableName + " where id = " + id,
                rs -> {
                    if (rs.next()) {
                        return itemFromResultSetRow(rs);
                    }
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    protected List<T> executeListQuery(String sql, QueryResultsInterface queryResultsInterface) {
        List<T> result = null;

        try {
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            result = (List<T>)queryResultsInterface.evaluateResults(rs);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logDbError(e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected T executeItemQuery(String sql, QueryResultsInterface queryResultsInterface) {
        T result = null;

        try {
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            result = (T)queryResultsInterface.evaluateResults(rs);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logDbError(e);
        }

        return result;
    }

    public int getLastInsertId() {
        int result = -1;

        try {
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery("select last_insert_rowid() as last_id");

            if (rs.next()) {
                result = rs.getInt("last_id");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logDbError(e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected Object executeQuery(String sql, QueryResultsInterface queryResultsInterface) {
        Object result = null;

        try {
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            result = queryResultsInterface.evaluateResults(rs);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logDbError(e);
        }

        return result;
    }

    protected void commit() {
        try {
            connection().commit();
        } catch (SQLException e) {
            logDbError(e);
        }
    }
}
