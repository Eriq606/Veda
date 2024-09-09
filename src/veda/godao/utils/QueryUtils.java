package veda.godao.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.postgresql.util.PSQLException;

import veda.EntityTable;
import veda.godao.DAO;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@SuppressWarnings({"rawtypes", "unchecked"})
public class QueryUtils {
    public static String[] getColumnNames(Class c) throws Exception{
        Field[] fields=c.getDeclaredFields();
        LinkedList<String> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote!=null){
                liste.add(annote.annotationType().getMethod(Constantes.COLUMN_VALUE).invoke(annote).toString());
            }
        }
        String[] colonnes=new String[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static String[] getColumnNamesWithoutPrimary(Class c) throws Exception{
        Field[] fields=c.getDeclaredFields();
        LinkedList<String> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            Annotation primary=f.getAnnotation(PrimaryKey.class);
            if(annote!=null&&primary==null){
                liste.add(annote.annotationType().getMethod(Constantes.COLUMN_VALUE).invoke(annote).toString());
            }
        }
        String[] colonnes=new String[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static String[] getNotNullColumnNames(Object o) throws Exception{
        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        LinkedList<String> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote==null){
                continue;
            }
            f.setAccessible(true);
            Object attr=f.get(o);
            if(attr==null){
                continue;
            }
            String columnName=annote.annotationType().getMethod("value").invoke(annote).toString();
            liste.add(columnName);
            f.setAccessible(false);
        }
        String[] colonnes=new String[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static String[] getNotNullColumnNamesWithoutPrimary(Object o) throws Exception{
        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        LinkedList<String> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            Annotation primary=f.getAnnotation(PrimaryKey.class);
            if(annote==null||primary!=null){
                continue;
            }
            f.setAccessible(true);
            Object attr=f.get(o);
            if(attr==null){
                continue;
            }
            String columnName=annote.annotationType().getMethod("value").invoke(annote).toString();
            liste.add(columnName);
            f.setAccessible(false);
        }
        String[] colonnes=new String[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static Field[] getColumnsWithoutPrimary(Class c) throws Exception{
        Field[] fields=c.getDeclaredFields();
        LinkedList<Field> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            Annotation primary=f.getAnnotation(PrimaryKey.class);
            if(annote!=null&&primary==null){
                liste.add(f);
            }
        }
        Field[] colonnes=new Field[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static Field[] getColumns(Class c) throws Exception{
        Field[] fields=c.getDeclaredFields();
        LinkedList<Field> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote!=null){
                liste.add(f);
            }
        }
        Field[] colonnes=new Field[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static Field[] getNotNullColumns(Object o) throws Exception{
        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        LinkedList<Field> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote==null){
                continue;
            }
            f.setAccessible(true);
            Object attr=f.get(o);
            if(attr==null){
                continue;
            }
            liste.add(f);
            f.setAccessible(false);
        }
        Field[] colonnes=new Field[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static Field[] getNotNullColumnsWithoutPrimary(Object o) throws Exception{
        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        LinkedList<Field> liste=new LinkedList<>();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            Annotation primary=f.getAnnotation(PrimaryKey.class);
            if(annote==null||primary!=null){
                continue;
            }
            f.setAccessible(true);
            Object attr=f.get(o);
            if(attr==null){
                continue;
            }
            liste.add(f);
            f.setAccessible(false);
        }
        Field[] colonnes=new Field[liste.size()];
        for(int i=0;i<colonnes.length;i++){
            colonnes[i]=liste.get(i);
        }
        return colonnes;
    }
    public static HashMap<String, Object> getNotNullColumnValues(Object o) throws Exception{
        HashMap<String, Object> colonnes=new HashMap<>();
        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote==null){
                continue;
            }
            f.setAccessible(true);
            Object attr=f.get(o);
            if(attr==null){
                continue;
            }
            String columnName=annote.annotationType().getMethod(Constantes.COLUMN_VALUE).invoke(annote).toString();
            colonnes.put(columnName, attr);
            f.setAccessible(false);
        }
        return colonnes;
    }
    public static HashMap<Field, String> getColumnsWithField(Class c) throws Exception{
        HashMap<Field, String> colonnes=new HashMap<>();
        Field[] fields=c.getDeclaredFields();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(Column.class);
            if(annote==null){
                continue;
            }
            String columnName=annote.annotationType().getMethod(Constantes.COLUMN_VALUE).invoke(annote).toString();
            colonnes.put(f, columnName);
        }
        return colonnes;
    }
    public static String getInsertQueryWithoutPrimary(Class c) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String[] colonnes=getColumnNamesWithoutPrimary(c);
        String query="insert into "+table+"(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+=colonnes[i]+")";
                break;
            }
            query+=colonnes[i]+", ";
        }
        query+=" values(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+="?)";
                break;
            }
            query+="?, ";
        }
        return query;
    }
    public static String getInsertQueryWithoutPrimary(Object o) throws Exception{
        Class c=o.getClass();
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String[] colonnes=getNotNullColumnNamesWithoutPrimary(o);
        String query="insert into "+table+"(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+=colonnes[i]+")";
                break;
            }
            query+=colonnes[i]+", ";
        }
        query+=" values(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+="?)";
                break;
            }
            query+="?, ";
        }
        return query;
    }
    public static <T>String getInsertQueryWithoutPrimary(Class<T> c, T[] objects) throws Exception{
        T o=objects[0];
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String[] colonnes=getNotNullColumnNamesWithoutPrimary(o);
        String query="insert into "+table+"(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+=colonnes[i]+")";
                break;
            }
            query+=colonnes[i]+", ";
        }
        query+=" values";
        for(int j=0;j<objects.length;j++){
            query+="(";
            for(int i=0;i<colonnes.length;i++){
                if(i==colonnes.length-1){
                    query+="?)";
                    break;
                }
                query+="?, ";
            }
            query+=",";
        }
        query=query.substring(0, query.length()-1);
        return query;
    }
    public static String getInsertQueryWithPrimary(Class c) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String[] colonnes=getColumnNames(c);
        String query="insert into "+table+"(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+=colonnes[i]+")";
                break;
            }
            query+=colonnes[i]+", ";
        }
        query+=" values(";
        for(int i=0;i<colonnes.length;i++){
            if(i==colonnes.length-1){
                query+="?)";
                break;
            }
            query+="?, ";
        }
        return query;
    }
    public static String getSelectQuery(Class c) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        Field primaryField=getPrimaryField(c);
        annote=primaryField.getAnnotation(Column.class);
        String primaryColumn=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="select * from "+table+" order by "+primaryColumn;
        return query;
    }
    public static String getSelectQuery(Class c, int limit, int offset) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        Field primaryField=getPrimaryField(c);
        annote=primaryField.getAnnotation(Column.class);
        String primaryColumn=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="select * from "+table+" order by "+primaryColumn+" limit "+limit+" offset "+offset;
        return query;
    }
    public static <T>String getSelectQuery(Class<T> c, T where, int limit, int offset) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        Field primaryField=getPrimaryField(c);
        annote=primaryField.getAnnotation(Column.class);
        String primaryColumn=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="select * from "+table+" where ";
        String[] columns=getNotNullColumnNames(where);
        for(String s:columns){
            query+=s+"=? and ";
        }
        query=query.substring(0, query.length()-5);
        query+=" order by "+primaryColumn+" limit "+limit+" offset "+offset;
        return query;
    }
    public static <T>String getSelectQuery(Class<T> c, T where, int limit, int offset, String[] order) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        Field primaryField=getPrimaryField(c);
        annote=primaryField.getAnnotation(Column.class);
        // String primaryColumn=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="select * from "+table+" where ";
        String[] columns=getNotNullColumnNames(where);
        for(String s:columns){
            query+=s+"=? and ";
        }
        query=query.substring(0, query.length()-5)+" order by ";
        for(String s:order){
            query+=s+", ";
        }
        query=query.substring(0, query.length()-2);
        query+=" limit "+limit+" offset "+offset;
        return query;
    }
    public static String getSelectQuery(Class c, Object where) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String[] columns=getNotNullColumnNames(where);
        String query="select * from "+table+" where ";
        Field primaryField=getPrimaryField(c);
        annote=primaryField.getAnnotation(Column.class);
        String primaryColumn=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        for(int i=0; i<columns.length; i++){
            if(i==columns.length-1){
                query+=columns[i]+" = ?";
                break;
            }
            query+=columns[i]+" = ? and ";
        }
        query+=" order by "+primaryColumn;
        return query;
    }
    public static String getUpdateQuery(Class c, Object change, Object where) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="update "+table+" set ";
        String[] changeColumns=getNotNullColumnNames(change);
        for(int i=0; i<changeColumns.length; i++){
            if(i==changeColumns.length-1){
                query+=changeColumns[i]+" = ? where ";
                break;
            }
            query+=changeColumns[i]+" = ?, ";
        }
        String[] whereColumns=getNotNullColumnNames(where);
        for(int i=0; i<whereColumns.length; i++){
            if(i==whereColumns.length-1){
                query+=whereColumns[i]+" = ?";
                break;
            }
            query+=whereColumns[i]+" = ? and ";
        }
        return query;
    }
    public static String getDeleteQuery(Class c, Object where) throws Exception{
        Annotation annote=c.getAnnotation(Table.class);
        String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
        String query="delete from "+table+" where ";
        String[] whereColumns=getNotNullColumnNames(where);
        for(int i=0; i<whereColumns.length; i++){
            if(i==whereColumns.length-1){
                query+=whereColumns[i]+" = ?";
                break;
            }
            query+=whereColumns[i]+" = ? and ";
        }
        return query;
    }
    public static Field getPrimaryField(Class c){
        Field[] fields=c.getDeclaredFields();
        for(Field f:fields){
            Annotation annote=f.getAnnotation(PrimaryKey.class);
            if(annote!=null){
                return f;
            }
        }
        return null;
    }
    public static PreparedStatement mapStatement(PreparedStatement statement, Field[] fields, Object o) throws Exception{
        PreparedStatement statemnt=statement;
        Class c=o.getClass();
        for(int i=0;i<fields.length;i++){
            Annotation annote=fields[i].getAnnotation(ForeignKey.class);
            Class type=fields[i].getType();
            fields[i].setAccessible(true);
            if(annote!=null){
                Field primary=getPrimaryField(type);
                primary.setAccessible(true);
                Object foreignId=primary.get(fields[i].get(o));
                String fieldType=primary.getType().getSimpleName();
                if(fieldType.equals("Integer")){
                    statemnt.setInt(i+1, (int)foreignId);
                }else if(fieldType.equals("String")){
                    statemnt.setString(i+1, foreignId.toString());
                }else if(fieldType.equals("Double")){
                    statemnt.setDouble(i+1, (double)foreignId);
                }else if(fieldType.equals("LocalDate")){
                    statemnt.setDate(i+1, Date.valueOf((LocalDate)foreignId));
                }else if(fieldType.equals("LocalDateTime")){
                    statemnt.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)foreignId));
                }else if(fieldType.equals("LocalTime")){
                    statement.setTime(i+1, Time.valueOf((LocalTime)foreignId));
                }
                primary.setAccessible(false);
                continue;
            }
            String fieldType=type.getSimpleName();
            String fieldName=fields[i].getName();
            if(fieldType.equals("Integer")){
                statemnt.setInt(i+1, (int)c.getMethod("get"+StringUtils.majStart(fieldName)).invoke(o));
            }else if(fieldType.equals("String")){
                statemnt.setString(i+1, fields[i].get(o).toString());
            }else if(fieldType.equals("Double")){
                statemnt.setDouble(i+1, (double)c.getMethod("get"+StringUtils.majStart(fieldName)).invoke(o));
            }else if(fieldType.equals("LocalDate")){
                statemnt.setDate(i+1, Date.valueOf((LocalDate)fields[i].get(o)));
            }else if(fieldType.equals("LocalDateTime")){
                statemnt.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)fields[i].get(o)));
            }else if(fieldType.equals("LocalTime")){
                statement.setTime(i+1, Time.valueOf((LocalTime)fields[i].get(o)));
            }
        }
        return statemnt;
    }
    public static Object[] mapStatement(PreparedStatement statement, Field[] fields, Object o, int offset) throws Exception{
        Object[] response=new Object[2];
        PreparedStatement statemnt=statement;
        Class c=o.getClass();
        int upOffset=offset;
        for(int i=offset;i<(fields.length+offset);i++){
            Annotation annote=fields[i-offset].getAnnotation(ForeignKey.class);
            Class type=fields[i-offset].getType();
            fields[i-offset].setAccessible(true);
            if(annote!=null){
                Field primary=getPrimaryField(type);
                primary.setAccessible(true);
                Object foreignId=primary.get(fields[i-offset].get(o));
                String fieldType=primary.getType().getSimpleName();
                if(fieldType.equals("Integer")){
                    statemnt.setInt(i+1, (int)foreignId);
                }else if(fieldType.equals("String")){
                    statemnt.setString(i+1, foreignId.toString());
                }else if(fieldType.equals("Double")){
                    statemnt.setDouble(i+1, (double)foreignId);
                }else if(fieldType.equals("LocalDate")){
                    statemnt.setDate(i+1, Date.valueOf((LocalDate)foreignId));
                }else if(fieldType.equals("LocalDateTime")){
                    statemnt.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)foreignId));
                }else if(fieldType.equals("LocalTime")){
                    statement.setTime(i+1, Time.valueOf((LocalTime)foreignId));
                }
                primary.setAccessible(false);
                upOffset++;
                continue;
            }
            String fieldType=type.getSimpleName();
            String fieldName=fields[i-offset].getName();
            if(fieldType.equals("Integer")){
                statemnt.setInt(i+1, (int)c.getMethod("get"+StringUtils.majStart(fieldName)).invoke(o));
            }else if(fieldType.equals("String")){
                statemnt.setString(i+1, fields[i-offset].get(o).toString());
            }else if(fieldType.equals("Double")){
                statemnt.setDouble(i+1, (double)c.getMethod("get"+StringUtils.majStart(fieldName)).invoke(o));
            }else if(fieldType.equals("LocalDate")){
                statemnt.setDate(i+1, Date.valueOf((LocalDate)fields[i-offset].get(o)));
            }else if(fieldType.equals("LocalDateTime")){
                statemnt.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)fields[i-offset].get(o)));
            }else if(fieldType.equals("LocalTime")){
                statement.setTime(i+1, Time.valueOf((LocalTime)fields[i].get(o)));
            }
            upOffset++;
        }
        response[0]=statemnt;
        response[1]=upOffset;
        return response;
    }
    public static Object mapResultSet(Connection connex, ResultSet result, Object object, HashMap<Field, String> columns, DAO dao) throws Exception{
        Object obj=object;
        Class c=object.getClass();
        for(Map.Entry<Field, String> entry:columns.entrySet()){
            Field f=entry.getKey();
            f.setAccessible(true);
            Class type=f.getType();
            Annotation annote=f.getAnnotation(ForeignKey.class);
            if(annote!=null){
                boolean recursive=(boolean)annote.annotationType().getMethod("recursive").invoke(annote);
                if(recursive==false){
                    continue;
                }
                Object foreignValue=type.getConstructor().newInstance();
                Field primary=getPrimaryField(type);
                String fieldType=primary.getType().getSimpleName();
                if(fieldType.equals("Integer")){
                    Integer val=Integer.valueOf(result.getInt(entry.getValue()));
                    type.getMethod("set"+StringUtils.majStart(primary.getName()), primary.getType()).invoke(foreignValue, val);
                }else if(fieldType.equals("String")){
                    primary.set(foreignValue, result.getString(entry.getValue()));
                }else if(fieldType.equals("Double")){
                    Double val=Double.valueOf(result.getDouble(entry.getValue()));
                    type.getMethod("set"+StringUtils.majStart(primary.getName()), primary.getType()).invoke(foreignValue, val);
                }else if(fieldType.equals("LocalDate")){
                    primary.set(foreignValue, result.getDate(entry.getValue()).toLocalDate());
                }else if(fieldType.equals("LocalDateTime")){
                    primary.set(foreignValue, result.getTimestamp(entry.getValue()).toLocalDateTime());
                }else if(fieldType.equals("LocalTime")){
                    primary.set(foreignValue, result.getTime(entry.getValue()).toLocalTime());
                }
                Object objet=dao.select(connex, type, foreignValue)[0];
                f.set(obj, objet);
                continue;
            }
            String fieldType=type.getSimpleName();
            String fieldName=f.getName();
            try{
                if(fieldType.equals("Integer")){
                    Integer val=Integer.valueOf(result.getInt(entry.getValue()));
                    c.getMethod("set"+StringUtils.majStart(fieldName), f.getType()).invoke(obj, val);
                }else if(fieldType.equals("String")){
                    f.set(obj, result.getString(entry.getValue()));
                }else if(fieldType.equals("Double")){
                    Double val=Double.valueOf(result.getDouble(entry.getValue()));
                    c.getMethod("set"+StringUtils.majStart(fieldName), f.getType()).invoke(obj, val);
                }else if(fieldType.equals("LocalDate")){
                    f.set(obj, result.getDate(entry.getValue()).toLocalDate());
                }else if(fieldType.equals("LocalDateTime")){
                    f.set(obj, result.getTimestamp(entry.getValue()).toLocalDateTime());
                }else if(fieldType.equals("LocalTime")){
                    f.set(obj, result.getTime(entry.getValue()).toLocalTime());
                }
            }catch(PSQLException e){
                if(e.getMessage().contains("not found in this ResultSet")){
                    continue;
                }else{
                    throw e;
                }
            }finally{
                f.setAccessible(false);
            }
        }
        return obj;
    }
    public static String getCountQuery(Class<?> c) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        String query="select count(*) from %s";
        Annotation annote=c.getAnnotation(Table.class);
        String tableName=annote.annotationType().getMethod("value").invoke(annote).toString();
        query=String.format(query, tableName);
        return query;
    }
    public static <T>String getCountQuery(Class<T> c, T where) throws Exception{
        String query="select count(*) from %s where ";
        Annotation annote=c.getAnnotation(Table.class);
        String tableName=annote.annotationType().getMethod("value").invoke(annote).toString();
        query=String.format(query, tableName);
        String[] columns=getNotNullColumnNames(where);
        for(String s:columns){
            query+=s+"=? and ";
        }
        query=query.substring(0, query.length()-5);
        return query;
    }
    public static String getCreateTableQuery(EntityTable table, boolean temporary){
        String query="create ";
        if(temporary){
            query+="temporary ";
        }
        query+="table "+table.getNom()+"(";
        for(Map.Entry<String, String> e:table.getColonnes().entrySet()){
            query+=e.getKey()+" "+e.getValue()+", ";
        }
        query=query.substring(0, query.length()-2)+")";
        return query;
    }
}
