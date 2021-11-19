package com.toavina.WebFramework.controller;

import com.toavina.WebFramework.annotation.Controller;
import com.toavina.WebFramework.annotation.UrlMap;
import com.toavina.WebFramework.annotation.UrlMapping;
import com.toavina.WebFramework.tool.ReturnParameters;
import com.toavina.dataaccessframework.db.AccessDb;
import com.toavina.WebFramework.model.Employer;

import java.sql.Connection;
import java.util.HashMap;

@Controller
public class EmployerController {
    Employer emp;

    @UrlMapping(url="test/test1",view = "test.jsp")
    public ReturnParameters listeEmployer(){
        HashMap<String, Object>data = new HashMap<>();
        try{
            Connection con = AccessDb.connect("org.postgresql.Driver", "postgres", "root", "jdbc:postgresql://localhost/test");
            Employer filtre = new Employer();
            Object[] liste = filtre.findAll(con, "");
            data.put("empList", liste);
        }catch (Exception e){
            e.printStackTrace();
        }
        ReturnParameters parameters = new ReturnParameters(data, "test.jsp");
        return parameters;
    }

    @UrlMapping(url="test/test2", view = "index.jsp")
    public void ajouterEmployer(){
        try{
            Connection con = AccessDb.connect("org.postgresql.Driver", "postgres", "root", "jdbc:postgresql://localhost/test");
            this.emp.insert(con);
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
