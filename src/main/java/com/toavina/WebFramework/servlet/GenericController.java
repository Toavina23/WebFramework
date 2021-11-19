package com.toavina.WebFramework.servlet;

import com.toavina.WebFramework.annotation.UrlMappingModel;
import com.toavina.WebFramework.tool.ClassLib;
import com.toavina.WebFramework.tool.ReturnParameters;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

@WebServlet(name = "GenericController", value = "/request/*")
public class GenericController extends HttpServlet {

    private Class[] controllers = null;
    private int counter = 0;
    private static String workingDirectory = "";
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ServletContext context =  request.getServletContext();
        workingDirectory = ClassLib.getWorkingDirectory(request.getServletContext());
        boolean formRequest = false;
        String url = new String(request.getRequestURL()).substring(request.getContextPath().length());
        if(url.contains("/form/")) formRequest = true;
        String cleanUrl = getCleanUrl(url);
        String[] splittedUrl = cleanUrl.split("-");
        String view = "";
        if(formRequest){
            /* Verifier par methode d'annotation*/
            Class controller = null;
            Method targetMethod = null;
            try{
                controller = Class.forName(workingDirectory+"."+splittedUrl[0]+"Controller");
            }catch (ClassNotFoundException ex){
                UrlMappingModel model = handleAnnotationFormRequest(url);
                controller = Class.forName(model.getControllerName());
                targetMethod = controller.getMethod(model.getMethodName());
                view = "/"+model.getView();
            }
            String[] parameters = getObjectParameters(request);
            String controllerAttrib = null;
            if(!(controllerAttrib = matchClass(controller, parameters)).equals("")){
                String attribName = controllerAttrib.split(" ")[1];
                String className = controllerAttrib.split(" ")[0];
                HashMap<String, String> validParams = finalAttributes(parameters, attribName, className);
                String[] values = finalValues(validParams, request, attribName);
                Class objClass = Class.forName(className);
                Object attribInstance = ClassLib.createInstance(objClass, validParams, values);
                Object controllerInstance = controller.newInstance();
                Method attribSetter = ClassLib.getSetter(controller, attribName, objClass);
                attribSetter.invoke(controllerInstance, attribInstance);

                if(targetMethod == null){
                    try{
                        targetMethod = controller.getMethod(splittedUrl[1]);
                    }catch (NoSuchMethodException ex){
                        throw ex;
                    }
                }
                /*String methodView = "";
                if(!(methodView = setRequestParameters(context, targetMethod, controllerInstance)).equals("/")) {
                    view = methodView;
                }*/
                targetMethod.invoke(controllerInstance);
            }
        }else{
            Class controller = null;
            boolean annotation = false;
            try {
                controller = Class.forName(workingDirectory+".controller."+splittedUrl[0]+"Controller");
            }catch (ClassNotFoundException ex){
                handleAnnotationRequest(url, context, request, response);
                annotation = true;
            }
            if(!annotation){
                Method targetMethod = controller.getMethod(splittedUrl[1], null);
                view = setRequestParameters(context, targetMethod, controller.newInstance());
            }
        }
        String pathView = "";
        if(splittedUrl.length > 1){
            pathView = "/" + splittedUrl[0]+"/"+splittedUrl[1]+".jsp";
        }
        if(!view.equals("")) pathView = view;
        context.getRequestDispatcher(pathView).forward(request,response);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            processRequest(request,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            processRequest(request,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Pour recuperer les parametres avec un "point" dans leur nom
     * */
    public static String[] getObjectParameters(HttpServletRequest request){
        Vector<String> tempStrings = new Vector<String>(1,1);
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
            if (className.contains("form/"))
                className = className.split("form/")[1];
        }
        int indexSlash=className.lastIndexOf("/");
        className=className.substring(indexSlash+1);
        return className;
    }

    /*
     * pour recuperer les valeurs de retour de les valeurs de retour
     * de la fonction et les mettres dans un hashmap
     * */

    public static String setRequestParameters(ServletContext context, Method method, Object obj) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        ReturnParameters retData = (ReturnParameters) method.invoke(obj);
        HashMap<String, Object> retValues = retData.getReturnValues();
        Object[] objKeys = retValues.keySet().toArray();
        for(int i=0;i<objKeys.length;i++){
            context.setAttribute((String)objKeys[i], retValues.get((String)objKeys[i]));
        }
        return "/"+retData.getViewUrl();
    }
    /*
    * Request by annotation handler
    * */
    public void handleAnnotationRequest(String url, ServletContext context, HttpServletRequest req, HttpServletResponse res) throws Exception{
        String filteredUrl = url.split("/request/")[1];
        UrlMappingModel model = null;
        if(this.controllers == null) {
            controllers = ClassLib.getClasses(workingDirectory);
            counter++;
            System.out.println("Searched the annotation class for "+counter+" times");
        }
        if((model=ClassLib.findMatchingUrl(filteredUrl, workingDirectory,this.controllers))!=null){
            Class controllerClass = Class.forName(model.getControllerName());
            Method method = controllerClass.getMethod(model.getMethodName());
            setRequestParameters(context, method, controllerClass);
            context.getRequestDispatcher("/"+model.getView()).forward(req,res);
        }
        else throw new UnsupportedOperationException();
    }

    public UrlMappingModel handleAnnotationFormRequest(String url) throws IOException, ClassNotFoundException {
        String filteredUrl = url.split("/request/form/")[1];
        UrlMappingModel model = null;
        if(this.controllers == null) {
            controllers = ClassLib.getClasses(workingDirectory);
            counter++;
            System.out.println("Searched the annotation class for "+counter+" times");
        }
        model=ClassLib.findMatchingUrl(filteredUrl, workingDirectory, this.controllers);
        if(model==null) throw new UnsupportedOperationException();
        return model;
    }
}

