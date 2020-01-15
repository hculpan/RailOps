package org.culpan.railops.dao;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.Table;
import org.culpan.railops.model.BaseModel;
import org.culpan.railops.util.AppHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SqlGenerator {
    /**
     * Converts a field to a string to be added to a sql insert/update statement.
     *
     * When adding a new data type in here, be sure to update BaseDao.setField().
     * @param field
     * @param o
     * @return
     * @throws IllegalAccessException
     */
    private String outputByType(Field field, Object o) throws IllegalAccessException {
        if (field.get(o) == null) return "null";

        StringBuilder result = new StringBuilder();

        if (field.getType().equals(String.class)) {
            result.append("'");
            result.append(field.get(o).toString());
            result.append("'");
        } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
            result.append(field.get(o).toString());
        } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            Boolean b = field.getBoolean(o);
            result.append((b ? "1" : "0"));
        } else {
            throw new RuntimeException(String.format("Unknown type %s for field %s", field.getType().getName(), field.getName()));
        }

        return result.toString();
    }

    public String generateDelete(BaseModel o) {
        return generateDelete(o.getClass(), String.format("where id = %d", o.getId()));
    }

    public String generateDelete(Class objectClass) {
        return generateDelete(objectClass, null);
    }

    public String generateDelete(Class objectClass, String whereClause) {
        if (!objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", objectClass.getName()));
        }

        Table table = (Table)objectClass.getAnnotation(Table.class);
        StringBuilder str = new StringBuilder(String.format("delete from %s ", table.name()));

        if (whereClause != null && !whereClause.isEmpty()) {
            str.append(whereClause);
        }

        return str.toString();
    }

    public String generateExists(BaseModel o) {
        return generateExists(o, String.format("id = %d", o.getId()));
    }

    public String generateExists(BaseModel o, String whereClause) {
        Class<? extends BaseModel> objectClass = o.getClass();

        if (!objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", objectClass.getName()));
        }

        return generateExists(objectClass, whereClause);
    }

    public String generateExists(Class<? extends BaseModel> objectClass, String whereClause) {
        if (!objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", objectClass.getName()));
        }

        Table table = objectClass.getAnnotation(Table.class);
        StringBuilder str = new StringBuilder(String.format("select id from %s ", table.name()));

        if (whereClause != null && !whereClause.isEmpty()) {
            str.append("where " + whereClause);
        }

        return str.toString();
    }

    @SuppressWarnings("unchecked")
    public String generateSelect(BaseModel o) {
        return generateSelect((Class<BaseModel>)o.getClass(), String.format("where id = %d", o.getId()));
    }

    public String generateSelect(Class o) {
        return generateSelect(o, null, null);
    }

    public String generateSelect(Class o, String whereClause) {
        return generateSelect(o, whereClause, null);
    }

    public String generateSelect(Class objectClass, String whereClause, String orderClause) {
        if (!objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", objectClass.getName()));
        }

        StringBuilder str = new StringBuilder("select id,");

        for (Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
                Column annotation = field.getAnnotation(Column.class);
                str.append((annotation.name().isBlank() ? field.getName() : annotation.name()) + ",");
            }
        }

        str.replace(str.length() - 1, str.length(), " ");

        Table table = (Table)objectClass.getAnnotation(Table.class);
        if (whereClause == null || whereClause.isEmpty()) {
            str.append(String.format("from %s ", table.name()));
        } else {
            str.append(String.format("from %s %s ", table.name(), whereClause));
        }

        if (orderClause != null && !orderClause.isEmpty()) {
            str.append(String.format("order by %s", orderClause));
        }

        return str.toString();
    }

    public String generateUpdate(BaseModel o) {
        if (!o.getClass().isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", o.getClass().getName()));
        }

        StringBuilder str = new StringBuilder("update ");
        try {
            Table table = o.getClass().getAnnotation(Table.class);
            str.append(table.name() + " set ");

            Class<?> objectClass = o.getClass();

            for (Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Column.class)) {
                    Column annotation = field.getAnnotation(Column.class);
                    str.append(String.format(" %s = %s,",
                            (annotation.name().isBlank() ? field.getName() : annotation.name()),
                            outputByType(field, o)));
                }
            }

            str.replace(str.length() - 1, str.length(), " ");

            str.append(String.format("where id = %d", o.getId()));
        } catch (IllegalAccessException e) {
            AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
            return "";
        }

        return str.toString();
    }

    public String generateInsert(BaseModel o) {
        if (!o.getClass().isAnnotationPresent(Table.class)) {
            throw new RuntimeException(String.format("Class %s does not have a Table annotation", o.getClass().getName()));
        }

        StringBuilder str = new StringBuilder("insert into ");
        try {
            Table table = o.getClass().getAnnotation(Table.class);
            str.append(table.name() + "(");

            Class<?> objectClass = o.getClass();

            StringBuilder valuesStr = new StringBuilder(" values(");

            for (Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Column.class)) {
                    Column annotation = field.getAnnotation(Column.class);
                    str.append((annotation.name().isBlank() ? field.getName() : annotation.name()) + ",");
                    valuesStr.append(outputByType(field, o) + ",");
                }
            }

            str.replace(str.length() - 1, str.length(), ")");
            valuesStr.replace(valuesStr.length() - 1, valuesStr.length(), ")");

            str.append(valuesStr.toString());

            str.replace(str.length() - 1, str.length(), ")");
        } catch (IllegalAccessException e) {
            AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
            return "";
        }

        return str.toString();
    }
}
