package veda.godao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedList;

import veda.EntityTable;
import veda.godao.annotations.Column;
import veda.godao.annotations.Table;
import veda.godao.utils.Constantes;
import veda.godao.utils.DAOConnexion;
import veda.godao.utils.QueryUtils;

@SuppressWarnings({"rawtypes","unchecked"})
public class DAO {
    private String driver;
    private String server;
    private String database;
    private String host;
    private String port;
    private String user, pwd;
    private boolean useSSL, allowKeyRetrieval;
    private int sgbd;
    
    public DAO() {
    }
    public DAO(String database, String host, String port, String user, String pwd, boolean useSSL, int sgbd) {
        this.sgbd = sgbd;
        this.database = database;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.useSSL = useSSL;
    }
    
    public DAO(String driver, String server, String database, String host, String port, String user, String pwd, boolean useSSL,
            boolean allowKeyRetrieval, int sgbd) {
        this.driver = driver;
        this.server=server;
        this.database = database;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.useSSL = useSSL;
        this.allowKeyRetrieval = allowKeyRetrieval;
        this.sgbd = sgbd;
    }
    
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public boolean isUseSSL() {
        return useSSL;
    }
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }
    public int getSgbd() {
        return sgbd;
    }
    public void setSgbd(int sgbd) {
        this.sgbd = sgbd;
    }
    public int insertWithoutPrimaryKey(Connection connex, Object o) throws Exception{
        Class c=o.getClass();
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getInsertQueryWithoutPrimary(c));
        Field[] fields=QueryUtils.getColumnsWithoutPrimary(c);
        statemnt=QueryUtils.mapStatement(statemnt, fields, o);
        try{
            statemnt.executeUpdate();
            if(opened){
                connect.commit();
            }
            Annotation annote=c.getAnnotation(Table.class);
            String table=annote.annotationType().getMethod(Constantes.TABLE_VALUE).invoke(annote).toString();
            Field primary=QueryUtils.getPrimaryField(c);
            Annotation column=primary.getAnnotation(Column.class);
            String columnName=column.annotationType().getMethod("value").invoke(column).toString();
            statemnt=connect.prepareStatement(String.format("select currval(pg_get_serial_sequence('%s', '%s'))", table, columnName));
            try(ResultSet result=statemnt.executeQuery()){
                if(result.next()){
                    return result.getInt(1);
                }
            }
            return 0;
        }catch(Exception ex){
            connect.rollback();
            throw ex;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public void insertWithPrimaryKey(Connection connex, Object o) throws Exception{
        Class c=o.getClass();
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        String query=QueryUtils.getInsertQueryWithPrimary(c);
        System.out.println(query);
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getInsertQueryWithPrimary(c));
        Field[] fields=QueryUtils.getColumns(c);
        statemnt=QueryUtils.mapStatement(statemnt, fields, o);
        try{
            statemnt.executeUpdate();
            if(opened){
                connect.commit();
            }
        }catch(Exception ex){
            connect.rollback();
            throw ex;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public <T>T[] select(Connection connex, Class<T> c) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getSelectQuery(c));
        HashMap<Field, String> columns=QueryUtils.getColumnsWithField(c);
        try{
            LinkedList liste=new LinkedList();
            ResultSet result=statemnt.executeQuery();
            while(result.next()){
                T obj=(T)c.getConstructor().newInstance();
                obj=(T)QueryUtils.mapResultSet(connect, result, obj, columns, this);
                liste.add(obj);
            }
            T[] objets=(T[])Array.newInstance(c, liste.size());
            for(int i=0;i<objets.length;i++){
                objets[i]=(T)liste.get(i);
            }
            return objets;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public <T>T[] select(Connection connex, Class<T> c, int limit, int offset) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getSelectQuery(c, limit, offset));
        HashMap<Field, String> columns=QueryUtils.getColumnsWithField(c);
        try{
            LinkedList liste=new LinkedList();
            ResultSet result=statemnt.executeQuery();
            while(result.next()){
                T obj=(T)c.getConstructor().newInstance();
                obj=(T)QueryUtils.mapResultSet(connect, result, obj, columns, this);
                liste.add(obj);
            }
            T[] objets=(T[])Array.newInstance(c, liste.size());
            for(int i=0;i<objets.length;i++){
                objets[i]=(T)liste.get(i);
            }
            return objets;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public <T>T[] select(Connection connex, Class<T> c, T where) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getSelectQuery(c, where));
        Field[] fields=QueryUtils.getNotNullColumns(where);
        statemnt=QueryUtils.mapStatement(statemnt, fields, where);
        HashMap<Field, String> columns=QueryUtils.getColumnsWithField(c);
        try{
            LinkedList liste=new LinkedList();
            ResultSet result=statemnt.executeQuery();
            while(result.next()){
                T obj=c.getConstructor().newInstance();
                obj=(T)QueryUtils.mapResultSet(connect, result, obj, columns, this);
                liste.add(obj);
            }
            T[] objets=(T[])Array.newInstance(c, liste.size());
            for(int i=0;i<objets.length;i++){
                objets[i]=(T)liste.get(i);
            }
            return objets;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public Object[] select_object(Connection connex, Class<?> c, Object where) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getSelectQuery(c, where));
        Field[] fields=QueryUtils.getNotNullColumns(where);
        statemnt=QueryUtils.mapStatement(statemnt, fields, where);
        HashMap<Field, String> columns=QueryUtils.getColumnsWithField(c);
        try{
            LinkedList liste=new LinkedList();
            ResultSet result=statemnt.executeQuery();
            while(result.next()){
                Object obj=c.getConstructor().newInstance();
                obj=QueryUtils.mapResultSet(connect, result, obj, columns, this);
                liste.add(obj);
            }
            Object[] objets=new Object[liste.size()];
            for(int i=0;i<objets.length;i++){
                objets[i]=liste.get(i);
            }
            return objets;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public void update(Connection connex, Object change, Object where) throws Exception{
        Class c=change.getClass();
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        String query=QueryUtils.getUpdateQuery(c, change, where);
        PreparedStatement statemnt=connect.prepareStatement(query);
        Field[] changeFields=QueryUtils.getNotNullColumns(change);
        Field[] whereFields=QueryUtils.getNotNullColumns(where);
        int offset=0;
        Object[] mapping=QueryUtils.mapStatement(statemnt, changeFields, change, offset);
        statemnt=(PreparedStatement)mapping[0];
        offset=(int)mapping[1];
        mapping=QueryUtils.mapStatement(statemnt, whereFields, where, offset);
        statemnt=(PreparedStatement)mapping[0];
        try{
            statemnt.executeUpdate();
            if(opened){
                connect.commit();
            }
        }catch(Exception ex){
            connect.rollback();
            throw ex;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public void delete(Connection connex, Object where) throws Exception{
        Class c=where.getClass();
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement(QueryUtils.getDeleteQuery(c, where));
        Field[] whereFields=QueryUtils.getNotNullColumns(where);
        statemnt=QueryUtils.mapStatement(statemnt, whereFields, where);
        try{
            statemnt.executeUpdate();
            if(opened){
                connect.commit();
            }
        }catch(Exception ex){
            connect.rollback();
            throw ex;
        }finally{
            if(opened){
                connect.close();
            }
            statemnt.close();
        }
    }
    public void customUpdate(Connection connex, String query) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        PreparedStatement statement=connect.prepareStatement(query);
        try{
            statement.executeUpdate();
            if(opened){
                connect.commit();
            }
        }catch(Exception e){
            connect.rollback();
            throw e;
        }finally{
            if(opened){
                connect.close();
            }
            statement.close();
        }
    }
    public int count(Connection connex, Class<?> c) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(driver, server, host, port, database, user, pwd, useSSL, allowKeyRetrieval);
            opened=true;
        }
        int compte=0;
        try(PreparedStatement statement=connect.prepareStatement(QueryUtils.getCountQuery(c))){
            try(ResultSet result=statement.executeQuery()){
                if(result.next()){
                    compte=result.getInt(1);
                }
            }
            return compte;
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public boolean isAllowKeyRetrieval() {
        return allowKeyRetrieval;
    }
    public void setAllowKeyRetrieval(boolean allowKeyRetrieval) {
        this.allowKeyRetrieval = allowKeyRetrieval;
    }
    public HashMap<String, Object>[] select(Connection connex, String query) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        try(PreparedStatement statement=connect.prepareStatement(query)){
            try(ResultSet result=statement.executeQuery()){
                LinkedList<HashMap<String, Object>> liste=new LinkedList<>();
                ResultSetMetaData metadata;
                HashMap<String, Object> hash;
                while(result.next()){
                    hash=new HashMap<>();
                    metadata=result.getMetaData();
                    for(int j=1;j<=metadata.getColumnCount();j++){
                        hash.put(metadata.getColumnName(j), result.getObject(j));
                    }
                    liste.add(hash);
                }
                HashMap<String, Object>[] resultat=new HashMap[liste.size()];
                for(int i=0;i<resultat.length;i++){
                    resultat[i]=liste.get(i);
                }
                return resultat;
            }
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
    public HashMap<String, Object>[] select(Connection connex, String[] columns, String table, String[] where) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        String fullQuery="select ";
        if(columns.length>0){
            for(String s:columns){
                fullQuery+=s+",";
            }
            fullQuery=fullQuery.substring(0, fullQuery.length()-1);
        }else{
            fullQuery+="*";
        }
        fullQuery+=" from "+table;
        if(where.length>0){
            fullQuery+=" where ";
            for(String s:where){
                fullQuery+=s+" and ";
            }
            fullQuery=fullQuery.substring(0, fullQuery.length()-5);
        }
        try(PreparedStatement statement=connect.prepareStatement(fullQuery)){
            try(ResultSet result=statement.executeQuery()){
                LinkedList<HashMap<String, Object>> liste=new LinkedList<>();
                ResultSetMetaData metadata;
                HashMap<String, Object> hash;
                while(result.next()){
                    hash=new HashMap<>();
                    metadata=result.getMetaData();
                    for(int j=1;j<=metadata.getColumnCount();j++){
                        hash.put(metadata.getColumnName(j), result.getObject(j));
                    }
                    liste.add(hash);
                }
                HashMap<String, Object>[] resultat=new HashMap[liste.size()];
                for(int i=0;i<resultat.length;i++){
                    resultat[i]=liste.get(i);
                }
                return resultat;
            }
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
    public void execute(Connection connex, String query) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.execute();
            if(opened){
                connect.commit();
            }
        }catch(Exception e){
            connect.rollback();
            throw e;
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
    public boolean exists(Connection connex, String tablename, String... conditions) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        String subquery="select 1 from "+tablename;
        if(conditions.length>0){
            subquery+=" where ";
        }
        for(String c:conditions){
            subquery+=c+" and ";
        }
        subquery=conditions.length>0?subquery.substring(0, subquery.length()-4):subquery;
        String query=String.format("select exists(%s)", subquery);
        try(PreparedStatement statement=connect.prepareStatement(query);ResultSet result=statement.executeQuery()){
            boolean exists=false;
            if(result.next()){
                exists=result.getBoolean(1);
            }
            return exists;
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
    public <T>boolean exists(Connection connex, Class<T> c, T where) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        PreparedStatement statemnt=connect.prepareStatement("select exists("+QueryUtils.getSelectQuery(c, where)+")");
        Field[] fields=QueryUtils.getNotNullColumns(where);
        statemnt=QueryUtils.mapStatement(statemnt, fields, where);
        try(ResultSet result=statemnt.executeQuery()){
            boolean exists=false;
            if(result.next()){
                exists=result.getBoolean(1);
            }
            return exists;
        }finally{
            statemnt.close();
            if(opened){
                connect.close();
            }
        }
    }
    public void createTable(Connection connex, EntityTable table, boolean temporary) throws Exception{
        boolean opened=false;
        Connection connect=connex;
        if(connect==null){
            connect=DAOConnexion.getConnexion(this);
            opened=true;
        }
        String query=QueryUtils.getCreateTableQuery(table, temporary);
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.execute();
        }finally{
            if(opened){
                connect.close();
            }
        }
    }
}
