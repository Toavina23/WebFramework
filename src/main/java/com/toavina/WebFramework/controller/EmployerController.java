package com.toavina.WebFramework.controller;

import com.toavina.WebFramework.annotation.UrlMap;
import com.toavina.WebFramework.annotation.UrlMapping;
import com.toavina.dataaccessframework.db.AccessDb;
import com.toavina.WebFramework.model.Employer;

import java.sql.Connection;
import java.util.HashMap;


public class EmployerController {
    Employer emp;

    @UrlMapping(url="test/test1",view = "test/SayHi.jsp")
    public HashMap<String, Object>listeEmployer(){
        HashMap<String, Object>data = new HashMap<>();
        try{
            Connection con = AccessDb.connect("org.postgresql.Driver", "toavina", "root", "jdbc:postgresql://localhost/test");
            Object[] liste = Employer.findAll(con, "", Employer.class);
            data.put("empList", liste);
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @UrlMapping(url="test/test2")
    public void ajouterEmployer(){
        try{
            Connection con = AccessDb.connect("org.postgresql.Driver", "toavina", "root", "jdbc:postgresql://localhost/test");
            this.emp.insertTable(con);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Employer getEmp() {
        return emp;
    }

    public void setEmp(Employer emp) {
        this.emp = emp;
    }
}
