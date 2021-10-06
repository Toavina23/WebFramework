package com.toavina.WebFramework.db;

import java.sql.Statement;
import java.lang.reflect.Method;
import java.sql.Connection;

public class Mere {
    public void insertTable(Connection con) throws Exception // insertion d'une table
    {
        Statement stat = null;
        try {
            String[] lAttribs = ClassLib.getAttribName(this.getClass()); /// recuperation des valeurs a inserer
            Method[] lMethods = ClassLib.matchMethods(getClass(), lAttribs, "get");
            lMethods = ClassLib.order(lAttribs, lMethods, "get");
            String sql = "insert into %s values %s";
            String values = ClassLib.dataRequest(this, lMethods, "insert");
            sql = String.format(sql, this.getClass().getSimpleName(), values);
            System.out.println(sql);
            stat = con.createStatement();
            stat.executeQuery(sql);
            stat.close();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        } finally {
            stat.close();
        }
    }

    public void insertNoCommit(Connection con) throws Exception{
        try {
            String[] lAttribs = ClassLib.getAttribName(this.getClass()); /// recuperation des valeurs a inserer
            Method[] lMethods = ClassLib.matchMethods(getClass(), lAttribs, "get");
            lMethods = ClassLib.order(lAttribs, lMethods, "get");
            String sql = "insert into %s values %s";
            String values = ClassLib.dataRequest(this, lMethods, "insert");
            sql = String.format(sql, this.getClass().getSimpleName(), values);
            System.out.println(sql);
            Statement stat = con.createStatement();
            stat.executeQuery(sql);
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public void updateTable(Object filtre, Connection con, String req) throws Exception {
        try {
            String[] lAttribs = ClassLib.getAttribName(this.getClass()); /// recuperation des valeurs a inserer
            Method[] lMethods = ClassLib.matchMethods(getClass(), lAttribs, "get");
            lMethods = ClassLib.order(lAttribs, lMethods, "get");
            String sql = "update %s set %s %s";
            String values = ClassLib.dataRequest(this, lMethods, "update");
            String cond = ClassLib.genCondition(filtre);
            sql = String.format(sql, this.getClass().getSimpleName(), values, cond);
            System.out.println(sql);
            Statement stat = con.createStatement();
            stat.executeUpdate(sql);
            con.commit();
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public void updateNoCommit(Object filtre, Connection con, String req) throws Exception {
        try {
            String[] lAttribs = ClassLib.getAttribName(this.getClass()); /// recuperation des valeurs a inserer
            Method[] lMethods = ClassLib.matchMethods(getClass(), lAttribs, "get");
            lMethods = ClassLib.order(lAttribs, lMethods, "get");
            String sql = "update %s set %s %s";
            String values = ClassLib.dataRequest(this, lMethods, "update");
            String cond = ClassLib.genCondition(filtre);
            sql = String.format(sql, this.getClass().getSimpleName(), values, cond);
            System.out.println(sql);
            Statement stat = con.createStatement();
            stat.executeUpdate(sql);
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public void deleteTable(Object filtre, Connection con) throws Exception {
        try {
            String cond = ClassLib.genCondition(filtre);
            String sql = "delete %s %s";
            sql = String.format(sql, this.getClass().getSimpleName(), cond);
            Statement stat = con.createStatement();
            stat.executeUpdate(sql);
            con.commit();
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public Object find(Connection con, String surplus) throws Exception {
        Object[] result = AccessDb.findObj(this, con, surplus);
        if (result.length != 1) {
            return null;
        }
        return result[0];
    }

    public static Object[] findAll(Connection con, String surplus, Class theClass) throws Exception {
        Object[] result = null;
        Object filtre = theClass.newInstance();
        result = AccessDb.findObj(filtre, con, surplus);
        return result;
    }
}
