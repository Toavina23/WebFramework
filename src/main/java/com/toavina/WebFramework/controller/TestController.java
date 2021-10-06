package com.toavina.WebFramework.controller;
import java.util.HashMap;

public class TestController {
    public HashMap<String, Object> SayHi(){
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("SayHiValue", "ito ehhh");
        return data;
    }
}
