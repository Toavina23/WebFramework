package com.toavina.WebFramework.testcontoller;

import com.toavina.WebFramework.annotation.Controller;
import com.toavina.WebFramework.annotation.UrlMapping;
import com.toavina.WebFramework.model.Employer;
import com.toavina.WebFramework.tool.ReturnParameters;
import com.toavina.dataaccessframework.db.AccessDb;

import java.sql.Connection;
import java.util.HashMap;

@Controller
public class TestEmpController {
    Employer emp;

    @UrlMapping(url="autrepackage",view = "test/SayHi.jsp")
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
}
