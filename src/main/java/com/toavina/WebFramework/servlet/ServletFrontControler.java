package com.toavina.WebFramework.servlet;

import com.toavina.WebFramework.db.ClassLib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet(name = "ServletFrontControler", value = "/request/*")
public class ServletFrontControler extends HttpServlet {

    private static String workingDirectory = "com.toavina.WebFramework";
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, ServletException, InvocationTargetException, IllegalAccessException, InstantiationException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ServletContext context = request.getServletContext();
        boolean putRequest = false;
        try{
            out.println("<h1>" + request.getRequestURL() + "</h1>");
            String url=new String(request.getRequestURL()).substring(request.getContextPath().length());
            url = url.split("/request/")[1];
            if (url.contains("put/")){
                url = url.split("put/")[1];
                putRequest = true;
            }
            int indexSlash=url.lastIndexOf("/");
            url=url.substring(indexSlash+1);
            out.println(url+"<br>");
            String[] splitter=url.split("-");
            if(splitter.length==1) out.println("URL non valide<br>");
            else
            {
                String controller=splitter[0]+"Controller";
                char[] c=new char[1];
                c[0]=new Character(controller.toCharArray()[0]).toUpperCase(controller.toCharArray()[0]);
                controller=new String(c)+controller.substring(1);
                if(putRequest){
                    String cheminClasse = workingDirectory + ".model." + controller.split("Controller")[0];//the class should have the same name as the controller
                    out.print(cheminClasse);
                    Class objClass = Class.forName(cheminClasse);
                    HashMap<String,String[]> validParams = getValidAttrib(objClass, request);
                    String strModel = (String) validParams.keySet().toArray()[0];
                    String[] validAttribs = validParams.get(strModel);
                    HashMap<String, String> attribType = getAttributes(objClass, strModel, validAttribs.length);

                }
                else{
                    try {
                        String complet = workingDirectory+".controller." + controller;
                        Class classe = Class.forName(complet);
                        out.println("classe trouvee<br>");
                        String methode = splitter[1];
                        out.println("classe : " + controller + "<br>");
                        Method methods = null;
                        try {
                            methods = classe.getMethod(methode,null);
                            out.println("methode : " + methode + "<br>");
                        } catch (Exception exc) {
                            out.println("Methode " + methode + " non trouvee");
                            exc.printStackTrace();
                        }
                        HashMap<String, Object> retData = (HashMap<String, Object>) methods.invoke(classe.newInstance());
                        Object[] objKeys = retData.keySet().toArray();
                        for(int i=0;i<objKeys.length;i++){
                            context.setAttribute((String)objKeys[i], retData.get((String)objKeys[i]));
                        }
                        String pathVue = "/"+splitter[0]+"/"+splitter[1]+".jsp";
                        out.print(pathVue);
                        context.getRequestDispatcher(pathVue).forward(request,response);
                    }catch (Exception e){
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }finally {
        out.close();
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request,response);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request,response);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Pour recuperer la valeur en string des parametres envoyées
     */
    private String[] getString_values(HttpServletRequest request, String[] attribNames){
        String[] values = new String[attribNames.length];
        for(int i=0;i<values.length;i++){
            values[i] = request.getParameter(attribNames[i]);
        }
        return values;
    }

    /*
    * Pour voir quelles sont les attibuts qui sont envoyés dans la requete
    * */
    private HashMap<String, String[]> getValidAttrib(Class model, HttpServletRequest request){
        String[] liste = new String[0];
        try {
            liste = ClassLib.getAttribName(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Vector<String> ret = new Vector<String>(1,1);
        Enumeration<String> enumParam = request.getParameterNames();
        String strModel = "";
        while(enumParam.hasMoreElements()){
            strModel += " "+enumParam.nextElement();
        }
        for(int i=0;i< liste.length;i++){
            if(strModel.contains(liste[i])){
                ret.add(liste[i]);
            }
        }
        HashMap<String, String[]> results = new HashMap<String, String[]>();
        results.put(strModel, liste);
        return results;
    }
    /*
    * Pour recuperer les attributs avec leurs types respectives
    * */
    private HashMap<String, String> getAttributes(Class objClass, String validAttribs, int lengthVal){
        Field[] fields = objClass.getDeclaredFields();
        String[] res = new String[lengthVal];
        HashMap<String, String> attribType = new HashMap<String, String>();
        for(int i=0;i<fields.length;i++){
            if(validAttribs.contains(fields[i].getName())) {
                attribType.put(fields[i].getName(), fields[i].getType().getSimpleName());
            }
        }
        return attribType;
    }
}
