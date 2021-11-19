package com.toavina.WebFramework.dao.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.toavina.WebFramework.dao.database.ManipDb;

public class Mere {/* the models that all classes will inherits */

    public void insert(Connection con) throws Exception {
        if (con != null) {
            PreparedStatement statement = ManipDb.createInsertStatement(this, con);
            System.out.println(statement.toString());
            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } finally {
                statement.close();
            }
        }
    }

    public void update(Connection con, Object filter) throws SQLException {
        if (con != null) {
            PreparedStatement statement = null;
            try {
                statement = ManipDb.createSelfUpdateStatement(this, con, filter);
                System.out.println(statement.toString());
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }
    }

    public void updateAll(Connection con, Object filtre) throws Exception{
        if (con != null) {
            PreparedStatement statement = null;
            try {
                statement = ManipDb.createGeneralUpdateStatement(this, con, filtre);
                System.out.println(statement.toString());
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }
    }

    public Object find(Connection con, String... extraCondition) throws SQLException {
        Object data = null;
        String extra = extraCondition.length > 0 ? extraCondition[0] : "";
        if(con!=null){
            PreparedStatement statement = null;
            try{
                statement = ManipDb.createSelectStatement(this, con, extra);
                System.out.println(statement.toString());
                ResultSet res = statement.executeQuery();
                Object[] result = ManipDb.fetchData(res, this.getClass());
                if(result.length>0){
                    data = result[0];
                }
            }catch(Exception e){    
                e.printStackTrace();
                throw e;
            }finally{
                try{
                    statement.close();
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return data;
    }

    public Object[] findAll(Connection con, String extraConditition) {
        Object[] data = null;
        if(con!=null){
            PreparedStatement statement = null;
            try{
                statement = ManipDb.createGeneralSelectStatement(this, con, extraConditition);
                System.out.println(statement.toString());
                ResultSet res = statement.executeQuery();
                data = ManipDb.fetchData(res, this.getClass());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
}
