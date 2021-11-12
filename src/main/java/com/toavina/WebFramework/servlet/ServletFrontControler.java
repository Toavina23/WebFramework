package com.toavina.WebFramework.servlet;

import com.toavina.WebFramework.annotation.UrlMap;
import com.toavina.WebFramework.annotation.UrlMappingModel;
import com.toavina.WebFramework.annotation.UrlMaps;
import com.toavina.WebFramework.tool.ClassLib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet(name = "ServletFrontControler", value = "/request/*")
public class ServletFrontControler extends HttpServlet {

    private static String workingDirectory = "";
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        workingDirectory = ClassLib.getWorkingDirectory(request.getServletContext());
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ServletContext context = request.getServletContext();
        boolean putRequest = false;
        String url=new String(request.getRequestURL()).substring(request.getContextPath().length());
        String filteredUrl = url.split("/request/")[1];
        UrlMappingModel model = null;
        if((model=ClassLib.findMatchingUrl(filteredUrl, workingDirectory))!=null){
            System.out.println("Annotation found");
            Class controllerClass = Class.forName(model.getControllerName());
            Method method = controllerClass.getMethod(model.getMethodName());
            setRequestParameters(context, method, controllerClass);
            context.getRequestDispatcher("/"+model.getView()).forward(request,response);
        }else{
            try{
                String cleanUrl = getCleanUrl(url);
                String controller=workingDirectory+".controller."+getControllerName(cleanUrl);
                UrlMap map = null;
                Class controllerClass = Class.forName(controller);
                out.println("<h1>" + request.getRequestURL() + "</h1>");

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
                    String methode = splitter[1];
                    if(putRequest){
                        out.print(controller);
                        String[] parameters = getObjectParameters(request);
                        String infoAttribControllerName=null;
                        if(!( infoAttribControllerName = matchClass(controllerClass, parameters)).equals("")){
                            String attribName = infoAttribControllerName.split(" ")[1];
                            String className = infoAttribControllerName.split(" ")[0];
                            HashMap<String,String> validParams = finalAttributes(parameters, attribName, className);
                            String [] values = finalValues(validParams, request,  attribName);

                            Class objClass = Class.forName(className);
                            Object attribControllerInstance = ClassLib.createInstance(objClass, validParams, values);
                            Object controllerInstance = controllerClass.newInstance();
                            Method attribSetter = ClassLib.getSetter(controllerClass,attribName, objClass);
                            attribSetter.invoke(controllerInstance, attribControllerInstance);
                            Method controllerMethod = null;
                            try{
                                controllerMethod = controllerClass.getMethod(methode);
                            }catch (NoSuchMethodException e){
                                System.out.println("Methode de l'url non trouvée");
                                e.printStackTrace();
                            }
                            controllerMethod.invoke(controllerInstance);
                            out.println("vita atreto");
                        }
                    }
                    else{
                        try {
                            Class classe = Class.forName(controller);
                            out.println("classe trouvee<br>");
                            out.println("classe : " + controller + "<br>");
                            Method methods = null;
                            try {
                                methods = classe.getMethod(methode,null);
                                out.println("methode : " + methode + "<br>");
                            } catch (Exception exc) {
                                out.println("Methode " + methode + " non trouvee");
                                exc.printStackTrace();
                            }
                            setRequestParameters(context, methods, classe);
                        }catch (Exception e){
                            e.printStackTrace();
                            throw e;
                        }
                    }
                    String pathVue = "/"+splitter[0]+"/"+splitter[1]+".jsp";
                    out.print(pathVue);
                    context.getRequestDispatcher(pathVue).forward(request,response);
                }
            }finally {
                out.close();
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * Pour recuperer les parametres avec un "point" dans leur nom
    * */
    public static String[] getObjectParameters(HttpServletRequest request){
        Vector<String>tempStrings = new Vector<String>(1,1);
        Enumeration<String> enumParam = request.getParameterNames();
        String temp = "";
        while(enumParam.hasMoreElements()){
            temp = enumParam.nextElement();
            if(temp.contains(".")){
                tempStrings.add(temp);
            }
        }
        String[] res = new String[tempStrings.size()];
        for(int i=0;i<tempStrings.size();i++){
            res[i] = tempStrings.elementAt(i);
        }
        return res;
    }

    /*
    * Pour verifier si le controlleur demandé posséde l'attribut des parametres
    * */
    public static String matchClass(Class controllerClass, String[] validParams){
        String model = validParams[0].split("\\.")[0];
        try{
            System.out.println(controllerClass.getName());
           Field attrib = controllerClass.getDeclaredField(model);
           return attrib.getType().getName()+" "+attrib.getName();
        }catch (NoSuchFieldException e){
            e.printStackTrace();
            return "";
        }
    }
    /*
    * pour matcher les attributs en parametre avec ceux du modele
    * */
    public static HashMap<String, String> finalAttributes(String[] validAttribs, String attribName, String className){
        Class modelClass = null;
        try{
            modelClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Classe du modèle non trouvée");
            e.printStackTrace();
        }
        Field[] modelFiels = modelClass.getDeclaredFields();
        HashMap<String, String> res = new HashMap<String,String>();
        for(int i=0; i< modelFiels.length;i++){
            if(!ClassLib.match(validAttribs, modelFiels[i].getName(), attribName+".").equals("")){
                res.put(modelFiels[i].getName(), modelFiels[i].getType().getSimpleName());
            }
        }
        return res;
    }
    /*
    * pour recuperer les valeurs des attributs dans la requete
    * */
    public static String[] finalValues(HashMap<String, String> finalAttribs, HttpServletRequest request, String prefix){
        Object[] params = finalAttribs.keySet().toArray();
        String[] values = new String[params.length];
        for(int i=0;i<params.length;i++){
            values[i] = request.getParameter(prefix+"."+(String) params[i]);
        }
        return values;
    }

    /*
    * pour recuperer l'annotation qui est sur la classe demandé
    * */

    public static UrlMap checkUrlAnnontation(Class objClass, String url) throws Exception{
        UrlMaps annotation = (UrlMaps) objClass.getDeclaredAnnotation(UrlMaps.class);
        System.out.println(annotation.toString());
        if(annotation !=null){
            System.out.println("find some annotation");
            UrlMap[] maps = annotation.value();
            for(UrlMap onemap: maps){
                if(url.equalsIgnoreCase(onemap.mappedUrl())){
                    return onemap;
                }
            }
        }
        return null;
    }
    /*
    * pour recuperer le nom du contolleur
    * */
    public static String getControllerName(String url){
        url = url.split("-")[0];
        System.out.println(ClassLib.capitalize(url)+"Controller");
        return ClassLib.capitalize(url)+"Controller";
    }

    /*
    * pour recuperer l'url sans les verbes de convetions
    * */
    public static String getCleanUrl(String rawUrl){
        String className = "";
        System.out.println(rawUrl);
        if(rawUrl.contains("/request/")) {
            className = rawUrl.split("/request/")[1];
            if (className.contains("put/"))
                className = className.split("put/")[1];
        }
        int indexSlash=className.lastIndexOf("/");
        className=className.substring(indexSlash+1);
        return className;
    }

    /*
    * pour recuperer les valeurs de retour de les valeurs de retour
    * de la fonction et les mettres dans un hashmap
    * */

    public static void setRequestParameters(ServletContext context, Method method, Class objClass) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        HashMap<String, Object> retData = (HashMap<String, Object>) method.invoke(objClass.newInstance());
        Object[] objKeys = retData.keySet().toArray();
        for(int i=0;i<objKeys.length;i++){
            context.setAttribute((String)objKeys[i], retData.get((String)objKeys[i]));
        }
    }
}