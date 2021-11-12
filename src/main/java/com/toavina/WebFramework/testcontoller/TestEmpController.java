package com.toavina.WebFramework.testcontoller;

import com.toavina.WebFramework.annotation.Controller;
import com.toavina.WebFramework.annotation.UrlMapping;
import com.toavina.WebFramework.model.Employer;
import com.toavina.dataaccessframework.db.AccessDb;

import java.sql.Connection;
import java.util.HashMap;

@Controller
public class TestEmpController {
    Employer emp;

    @UrlMapping(url="autrepackage",view = "test/SayHi.jsp")
    public HashMap<String, Object> listeEmployer(){
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
}
