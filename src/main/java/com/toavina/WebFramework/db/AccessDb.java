package com.toavina.WebFramework.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.Calendar;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Vector;


public class AccessDb {

    /**
     * @apiNote Function to connect to any database just by changing the database
     * @param driverName: the name of the driver
     * @param user: the user to connect to the database
     * @param password: the password of the user
     * @param database: the name of the database
     * @return java.sql.Connection
     */
    public static Connection connnect(String driverName, String user, String password, String database) throws Exception{
        Connection con = null;
            try{
                Class.forName(driverName);
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+database, user, password);
                if(con == null){
                  Exception e = new Exception("An error occured during the attempt to connnect...");
                  throw e;
                }
            }catch(Exception ex){
                ex.printStackTrace();
                throw ex;
            }
        return con;
    }

    public static Object toObject(Class theClass,Object[] valAttrib,String [] nomAttrib) throws Exception
    {
        Method [] lMethod = ClassLib.matchMethods(theClass, nomAttrib,"set");
        lMethod = ClassLib.order(nomAttrib, lMethod, "set");
        Object obj=theClass.newInstance();
        for(int i=0;i<lMethod.length;i++)
        {
            String param = lMethod[i].getParameterTypes()[0].getSimpleName();
            if(param.equalsIgnoreCase("String"))
            {
                lMethod[i].invoke(obj, (String)valAttrib[i]);
            }
            else if(param.equalsIgnoreCase("Integer") || param.equalsIgnoreCase("int"))
            {
                Integer temp = (Integer)valAttrib[i];
                lMethod[i].invoke(obj, temp.intValue());
            }
            else if(param.equalsIgnoreCase("Double"))
            {
                Double temp = (Double)valAttrib[i];
                lMethod[i].invoke(obj, temp.doubleValue());
            }
            else if(param.equalsIgnoreCase("Float"))
            {
                Double temp = (Double) valAttrib[i];
                lMethod[i].invoke(obj, temp.floatValue());
            }
            else if(param.equalsIgnoreCase("Calendar"))
            {
                java.sql.Timestamp date =(java.sql.Timestamp)valAttrib[i];
                Calendar daty = Calendar.getInstance();
                daty.set(date.getYear(),date.getMonth(), date.getDate());
                lMethod[i].invoke(obj, daty);
            }
            else if(param.equalsIgnoreCase("Date"))
            {
                java.sql.Date date = (java.sql.Date)valAttrib[i];
                Date daty;
                if(date==null){
                    daty=null;
                }else{
                    daty = new Date(date.getYear(), date.getMonth(), date.getDate());
                    lMethod[i].invoke(obj,daty);
                }
            }
        }
        return obj;
    }

    public static Object[] find(String sql,Object filtre,Connection con)throws Exception
    {
        Class theClass = filtre.getClass();
        String [] attribName = ClassLib.getAttribName(theClass);
        Method [] lMethods = ClassLib.matchMethods(theClass, attribName, "set");
        lMethods = ClassLib.order(attribName, lMethods, "set");
        Statement stat = con.createStatement();
        ResultSet res = stat.executeQuery(sql);
        Vector<Object>vec = new Vector<Object>(1,1);
        Object temp = new Object();
        while(res.next()){
            Object [] lValues = new Object[attribName.length];
            for(int i=0;i<attribName.length;i++)
            {
                String param =lMethods[i].getParameterTypes()[0].getSimpleName();
                if(param.equalsIgnoreCase("Integer") || param.equalsIgnoreCase("Double") 
                    || param.equalsIgnoreCase("int") || param.equalsIgnoreCase("Float") || param.equalsIgnoreCase("float"))
                {
                    lValues[i] = res.getObject(attribName[i]);
                }
                else if(param.equalsIgnoreCase("String"))
                {
                    lValues[i] = res.getString(attribName[i]);
                }
                else if(param.equalsIgnoreCase("Calendar"))
                {
                    lValues[i] = res.getTimestamp(attribName[i]);
                }
                else if(param.equalsIgnoreCase("Date")){
                    lValues[i] = res.getDate(attribName[i]);
                }
                else
                {
                    IllegalArgumentException e = new IllegalArgumentException("type de parametre pas encore pris en compte");
                    throw e;
                }
            }
            temp = toObject(theClass, lValues, attribName);
            vec.add(temp);
        }
        res.close();
        stat.close();
        Object [] obj = vec.toArray();
        return obj;
    }

    public static Object[] findObj(Object filtre,Connection con,String req)throws Exception
    {
        String sql = "select * from %s %s %s";
        String cond = ClassLib.genCondition(filtre);
        sql = String.format(sql, filtre.getClass().getSimpleName(),cond,req);
        System.out.println(sql);
        Object [] res = find(sql, filtre, con);
        return res;
    }

    public static int countObject(Object filtre,Connection con,String req)throws Exception{
        int number = 0;
        String sql = "select count(*) as total from %s %s %s";
        String cond = ClassLib.genCondition(filtre);
        sql = String.format(sql, filtre.getClass().getSimpleName(),cond,req);
        System.out.println(sql);
        Statement stat = con.createStatement();
        ResultSet res = stat.executeQuery(sql);
        while (res.next()){
            number = res.getInt("total");
        }
        return number;
    }
    
}   
