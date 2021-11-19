package com.toavina.WebFramework.controller;

import com.toavina.WebFramework.annotation.Controller;
import com.toavina.WebFramework.annotation.UrlMapping;
import com.toavina.WebFramework.model.Personne;
import com.toavina.dataaccessframework.db.AccessDb;

import java.sql.Connection;

@Controller
public class PersonneController {
    Personne personne;

    @UrlMapping(url = "ajoutPersonne", view = "index.jsp")
    public void ajouterPersonne() throws Exception{
        Connection con = AccessDb.connect("org.postgresql.Driver", "postgres", "root", "jdbc:postgresql://localhost/test");
        this.personne.insert(con);
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }
}
