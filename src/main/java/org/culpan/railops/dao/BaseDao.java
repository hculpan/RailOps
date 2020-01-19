package org.culpan.railops.dao;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToMany;
import org.culpan.railops.dao.annotations.OneToOne;
import org.culpan.railops.dao.interfaces.PostAddOrUpdate;
import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.BaseModel;
import org.culpan.railops.util.AppHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseDao<T extends BaseModel> {
    private final static SqlGenerator sqlGenerator = new SqlGenerator();

    private static Connection conn;

    protected static String lastSql;

    public interface QueryResultsInterface {
        Object evaluateResults(ResultSet set) throws SQLException;
    }

    public void logDbError(Throwable t) {
        AppHelper.showExceptionInfo(t, "Database Error",
                "There was a problem while executing a database statement: \n  " + lastSql);
    }

    protected void initDb(String dbPath) throws ClassNotFoundException, SQLException {
        conn = null;

        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        conn.setAutoCommit(false);
    }

    public static void closeDb() throws SQLException {
        if (conn != null) {
            conn.close();;
            conn = null;
        }
    }

    public Connection connection() {
        if (conn == null) {
            try {
                initDb(String.format("%s%s/.railops/railops.sqlite", System.getProperty("user.home"), File.separator));
            } catch (ClassNotFoundException | SQLException e) {
                logDbError(e);
            }
        }

        return conn;
    }

    public boolean exists(T item) {
        String sql = sqlGenerator.generateExists(item);
        return (Boolean)executeQuery(sql, rs -> rs.next());
    };

    public boolean exists(T item, String whereClause) {
        String sql = sqlGenerator.generateExists(item);
        return (Boolean)executeQuery(sql, rs -> rs.next());
    };

    public boolean exists(Class<? extends BaseModel> c, String whereClause) {
        return (Boolean)executeQuery(sqlGenerator.generateExists(c, whereClause), rs -> rs.next());
    }

    public List<T> load() {
        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        return executeListQuery(sqlGenerator.generateSelect(c),
                rs -> {
                    List<T> result = new ArrayList<>();

                    while (rs.next()) result.add(itemFromResultSetRow(rs));

                    return result;
                });
    }

    public boolean addOrUpdate(T item, boolean autocommit) {
        String sql;
        if (exists(item)) {
            sql = sqlGenerator.generateUpdate(item);
            boolean result = executeUpdate(sql, autocommit);
            if (result) {
                return addOrUpdateRelated(item, autocommit);
            }
            return false;
        } else {
            sql = sqlGenerator.generateInsert(item);
            if (executeUpdate(sql, autocommit)) {
                item.setId(getLastInsertId());
                return addOrUpdateRelated(item, autocommit);
            } else {
                return false;
            }
        }
    }

    protected boolean addOrUpdateRelated(T item, boolean autocommit) {
        boolean result = true;
        try {
            Class<? extends BaseModel> c = item.getClass();
            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(OneToOne.class)) {
                    OneToOne a = field.getAnnotation(OneToOne.class);
                    if (a.persist() && BaseModel.class.isAssignableFrom(field.getType())) {
                        Class<? extends BaseDao> daoClass = a.dao();
                        BaseDao dao = daoClass.getDeclaredConstructor().newInstance();
                        BaseModel data = (BaseModel)field.get(item);
                        Field foreignKeyField = data.getClass().getDeclaredField(a.fieldName());
                        if (foreignKeyField.getType() != Integer.class && foreignKeyField.getType() != int.class) {
                            throw new RuntimeException("Attempt to assign foreign id to a non-numeric field");
                        }
                        foreignKeyField.set(data, item.getId());
                        result = dao.addOrUpdate(data, autocommit);
                    }
                } else if (field.isAnnotationPresent(OneToMany.class)) {
                    OneToMany a = field.getAnnotation(OneToMany.class);
                    if (a.persist() && Collection.class.isAssignableFrom(field.getType())) {
                        Class<? extends BaseDao> daoClass = a.dao();
                        BaseDao dao = daoClass.getDeclaredConstructor().newInstance();
                        Collection<? extends BaseModel> data = (Collection<? extends BaseModel>) field.get(item);
                        for (BaseModel b : data) {
                            Field foreignKeyField = b.getClass().getDeclaredField(a.keyFieldName());
                            foreignKeyField.setAccessible(true);
                            if (foreignKeyField.getType() != Integer.class && foreignKeyField.getType() != int.class) {
                                throw new RuntimeException("Attempt to assign foreign id to a non-numeric field");
                            }
                            foreignKeyField.set(b, item.getId());
                            if (!dao.addOrUpdate(b, autocommit)) {
                                result = false;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            AppHelper.showExceptionInfo(t, t.getLocalizedMessage());
            result = false;
        }

        if (result && this instanceof PostAddOrUpdate) ((PostAddOrUpdate) this).addOrUpdateComplete(item, autocommit);

        return result;
    }

    public boolean addOrUpdate(T item) {
        return addOrUpdate(item, true);
    }

    public boolean delete(T item) {
        return delete(item, true);
    }

    public boolean delete(T item, boolean autocommit) {
        return executeUpdate(sqlGenerator.generateDelete(item), autocommit);
    }

    public boolean delete(String whereClause) {
        return delete(whereClause, true);
    }

    public boolean delete(String whereClause, boolean autocommit) {
        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        return executeUpdate(sqlGenerator.generateDelete(c, "where " + whereClause));
    }

    public boolean deleteAll() {
        return deleteAll(true);
    }

    public boolean deleteAll(boolean autocommit) {
        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        return executeUpdate(sqlGenerator.generateDelete(c), autocommit);
    }

    public T findById(int id) {
        if (id <= 0) return null;

        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        String sql = sqlGenerator.generateSelect(c, String.format("where id = %d ", id));
        return executeItemQuery(sql, rs -> {
           if (rs.next()) return itemFromResultSetRow(rs);
           else return null;
        });
    }

    public List<T> find(String whereClause) {
        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        String sql = sqlGenerator.generateSelect(c, "where " + whereClause);
        return executeListQuery(sql, rs -> {
            List<T> result = new ArrayList<>();

            while (rs.next()) {
                result.add(itemFromResultSetRow(rs));
            }

            return result;
        });
    }

    /**
     * Sets the value of the field for the given object, pulling the data
     * from the ResultSet.
     *
     * If adding handling for a new data type here, be sure to also update
     * SqlGenerator.outputByType().
     * @param field
     * @param result
     * @param rs
     * @throws SQLException
     */
    private void setField(Field field, BaseModel result, ResultSet rs)
            throws SQLException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException,
                   InvocationTargetException, InstantiationException {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            String dbFieldName = (column.name().isBlank() ? field.getName() : column.name());
            if (field.getType().equals(String.class)) {
                field.set(result, rs.getString(dbFieldName));
            } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                field.set(result, rs.getInt(dbFieldName));
            } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                field.set(result, (rs.getInt(dbFieldName) > 0));
            } else {
                throw new RuntimeException(String.format("Unknown type %s for field %s", field.getType().getName(), field.getName()));
            }
        } else if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne a = field.getAnnotation(OneToOne.class);
            String fieldName = a.fieldName();
            Class<? extends BaseDao> c = a.dao();
            BaseDao dao = c.getDeclaredConstructor().newInstance();
            Field foreignField = result.getClass().getDeclaredField(fieldName);
            foreignField.setAccessible(true);
            int foreignId = foreignField.getInt(result);
            field.set(result, dao.findById(foreignId));
        } else if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany a = field.getAnnotation(OneToMany.class);
            Class<? extends BaseDao> c = a.dao();
            BaseDao dao = c.getDeclaredConstructor().newInstance();
            field.set(result, dao.find(String.format("%s = %d", a.foreignKeyName(), result.getId())));
        }
    }

    @SuppressWarnings("unchecked")
    protected T itemFromResultSetRow(ResultSet rs) {
        Class c = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        try {
            T result = (T)c.getDeclaredConstructor().newInstance();

            result.setId(rs.getInt("id"));

            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true);
                setField(field, result, rs);
            }

            if (this instanceof PostInitialize) {
                ((PostInitialize) this).initializeComplete(result);
            }

            return result;
        } catch (Throwable e) {
            AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
            return null;
        }
    }

    public T find(T item) {
        return findById(item.getId());
    };

    @SuppressWarnings("unchecked")
    protected List<T> executeListQuery(String sql, QueryResultsInterface queryResultsInterface) {
        lastSql = sql;
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
        lastSql = sql;
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
        lastSql = sql;
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

    protected boolean executeUpdate(String sql, boolean commit) {
        lastSql = sql;
        try {
            int rowCount;
            Statement stmt = connection().createStatement();
            rowCount = stmt.executeUpdate(sql);
            stmt.close();
            if (commit) connection().commit();
            return true;
        } catch (SQLException e) {
            logDbError(e);
            return false;
        }
    }

    protected boolean executeUpdate(String sql) {
        return executeUpdate(sql, true);
    }

    public void rollback() {
        try {
            connection().rollback();
        } catch (SQLException e) {
            logDbError(e);
        }
    }

    public void commit() {
        try {
            connection().commit();
        } catch (SQLException e) {
            logDbError(e);
        }
    }
}
