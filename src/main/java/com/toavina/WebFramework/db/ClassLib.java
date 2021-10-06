package com.toavina.WebFramework.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClassLib {
    /**
     * @apiNote This function is user to find if the string given matches with a
     *          string of the list
     * @param obj:    the string to match
     * @param attrib: list of the string to compare with
     * @return true if matching false if not
     */
    public static boolean match(String obj, String[] attrib) {
        for (int i = 0; i < attrib.length; i++)
            if (obj.equalsIgnoreCase(attrib[i]))
                return true;
        return false;
    }

    public static Method[] matchMethods(Class objClass, String[] attribs, String funcType) {
        List<Method> methodList = new ArrayList<Method>();
        Method[] classMethods = objClass.getMethods();
        for (int x = 0; x < attribs.length; x++)
            for (int i = 0; i < classMethods.length; i++)
                if (classMethods[i].getName().startsWith(funcType))
                    if (attribs[x].equalsIgnoreCase(classMethods[i].getName().split(funcType)[1]))
                        methodList.add(classMethods[i]);
        Method[] ret = new Method[methodList.size()];
        int index = 0;
        for (Method method : methodList) {
            ret[index] = method;
            index++;
        }
        return ret;
    }

    public static Method[] order(String[] lAttrib, Method[] lMethods, String funcType) {
        Vector<Method> vec = new Vector<Method>(1, 1);
        for (int x = 0; x < lAttrib.length; x++) {
            for (int i = 0; i < lMethods.length; i++) {
                if (lMethods[i].getName().split(funcType)[1].equalsIgnoreCase(lAttrib[x]))
                    vec.add(lMethods[i]);
            }
        }
        Method[] liste = new Method[vec.size()];
        for (int i = 0; i < vec.size(); i++) {
            liste[i] = (Method) vec.elementAt(i);
        }
        return liste;
    }

    public static String[] getAttribName(Class objclass) throws Exception {
        String[] lName = null;
        try {
            Field[] lAttribs = objclass.getDeclaredFields();
            lName = new String[lAttribs.length];
            for (int i = 0; i < lAttribs.length; i++) {
                lName[i] = lAttribs[i].getName();
            }
        } catch (Exception e) {
            ClassNotFoundException er = new ClassNotFoundException("la classe que vous avez donner n'existe pas");
            throw er;
        }
        return lName;
    }

    public static String transformDate(Object date) {
        String dat = "";
        if (date instanceof Calendar) {
            Calendar cal = (Calendar) date;
            dat = Integer.toString(cal.get(Calendar.YEAR)) + "-" + (Integer.toString(cal.get(Calendar.MONTH))) + "-"
                    + Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        } else if (date instanceof Date) {
            Date realDate = (Date) date;
            dat = Integer.toString(realDate.getYear()) + "-" + Integer.toString(realDate.getMonth()) + "-"
                    + Integer.toString(realDate.getDate());
        }

        return dat;
    }

    public static String toDate(Object date) {
        String dat = "to_date(" + "'" + transformDate(date) + "'" + ",'yyyy-mm-dd')";
        return dat;
    }

    public static String concat(Object valeur, String attribName) {
		String req = "";
		if (valeur instanceof Integer)
			req = req + attribName + "=" + Integer.toString((Integer) valeur);
		else if (valeur instanceof Double)
			req = req + attribName + "=" + Double.toString((Double) valeur);
		else if (valeur instanceof  Float)
			req = req + attribName + "=" + Float.toString((Float) valeur);
		else if (valeur instanceof String) {
			if(((String) valeur).contains("%")) req = req + attribName + " like '" + (String) valeur + "'";
			else req = req + attribName + "=" + "'" + (String) valeur + "'";
		}
		else if (valeur instanceof Calendar)
			req = req + attribName + " like " + "to_date(" + "'" + transformDate((Calendar) valeur) + "'"
					+ ",'yyyy-mm-dd')";
		else if (valeur instanceof Date)
			req = req + attribName + " like " + "to_date(" + "'" +transformDate((Date) valeur) + "'"
					+ ", 'yyyy-mm-dd')";
		return req;
	}

    public static String convert(Object obj, String nameAttrib, Class thisCl) {
		if (obj instanceof Integer)
			return Integer.toString((Integer) obj);
		else if (obj instanceof Double)
			return Double.toString((Double) obj);
		else if (obj instanceof Float)
			return Float.toString((Float) obj);
		else if (obj instanceof String)
			if (!nameAttrib.endsWith("id"))
				return "'" + (String) obj + "'";
			else {
				if(nameAttrib.split("id")[0].equalsIgnoreCase(thisCl.getSimpleName())){
					String res = "Concat('%s',%s)";
					res = String.format(res, thisCl.getSimpleName()+"-", "nextval('"+nameAttrib+"')");
					return res;
				}
				else return "'" + (String) obj + "'";
			}
		else if (obj instanceof Calendar) {
			String req = "to_date(" + "'" + transformDate((Calendar) obj) + "'" + ",'yyyy-mm-dd')";
			return req;
		}
		else if(obj instanceof Date){
			String req = "to_date(" + "'" + transformDate((Date) obj) + "'" + ",'yyyy-mm-dd')";
			return req;
		}
		return null;
	}

    public static Method[] getAttribMethod(Class objclass) {
		Method[] lMethods = objclass.getDeclaredMethods();
		Vector<Method>vec = new Vector<Method>(1, 1);
		for (int i = 0; i < lMethods.length; i++) {
			if (lMethods[i].getName().startsWith("get"))
				vec.add(lMethods[i]);
		}
		Method[] retour = new Method[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			retour[i] = (Method) vec.elementAt(i);
		}
		return retour;
	}

    public static Object[] callMethods(Method[] lMethod, Object obj) {
		Object[] attribs = new Object[lMethod.length];
		try {
			for (int i = 0; i < lMethod.length; i++) {
				attribs[i] = lMethod[i].invoke(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attribs;
	}

    public static String parMillier(String nb) {
		String result = "";
		int longueur = nb.length();
		int virgule = nb.indexOf(".");
		System.out.println("virg=" + virgule);
		int ampina = 0;
		int longDecimale = virgule;
		if (virgule == -1)
			longDecimale = longueur;
		if (longDecimale % 3 == 0)
			ampina = (longDecimale / 3) - 1;
		else
			ampina = longDecimale / 3;
		int alavany = longueur + ampina;
		char[] c = new char[alavany];
		int j = alavany - 1;
		int k = 0;
		for (int i = longueur - 1; i >= 0; i--) {
			if (i > virgule - 1) {
				c[j] = nb.charAt(i);
				j--;
			} else {
				k++;
				c[j] = nb.charAt(i);
				j--;
				if (k % 3 == 0) {
					if (j != 0) {
						c[j] = ' ';
						j--;
						k = 0;
					}
				}
			}
		}
		result = new String(c);
		return result;
	}

    public Object[] callMethodsDB(Method[] lMethod, Object obj, Connection con) {
		Object[] attribs = new Object[lMethod.length];
		try {
			for (int i = 0; i < lMethod.length; i++) {
				if (lMethod[i].getParameterTypes().length != 0) {
					attribs[i] = lMethod[i].invoke(obj, con);
				} else {
					if (lMethod[i].getName().startsWith("get")) {
						if (lMethod[i].getReturnType().getSimpleName().equals("Calendar")) {
							Calendar cal = (Calendar) lMethod[i].invoke(obj);
							Date date = new Date(cal.get(1), cal.get(2), cal.get(5));
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							attribs[i] = sdf.format(date);
						} else if (lMethod[i].getReturnType().getSimpleName().equals("int")) {
							String str = String.valueOf((int) lMethod[i].invoke(obj));
							attribs[i] = parMillier(str);
						} else if (lMethod[i].getReturnType().getSimpleName().equals("Double")) {
							String str = String.valueOf((Double) lMethod[i].invoke(obj));
							attribs[i] = parMillier(str);
						} else {
							attribs[i] = lMethod[i].invoke(obj);
						}
					} else
						attribs[i] = lMethod[i].invoke(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attribs;
	}

    public static String dataRequest(Object obj, Method[] lMethod, String reqType) throws Exception {
		String req = "";
		Class thisClass = obj.getClass();
		String[] nameAttrib = getAttribName(thisClass);
		Method[] liste = order(nameAttrib, lMethod, "get");
		Object[] valueAttrib = callMethods(liste, obj);
		if (reqType.equalsIgnoreCase("update") || reqType.equalsIgnoreCase("find")) {
			req = concat(valueAttrib[0], nameAttrib[0]);
			for (int i = 1; i < valueAttrib.length; i++) {
				if(valueAttrib[i]!=null){
					if(!nameAttrib[i].equalsIgnoreCase(thisClass.getSimpleName()+"id")){
						String temp = concat(valueAttrib[i], nameAttrib[i]);
						req = req + "," + temp;
					}
				}
			}
		} else if (reqType.equalsIgnoreCase("insert")) {
			req = req + "(";
			for (int i = 0; i < valueAttrib.length; i++) {
				if (i == valueAttrib.length - 1)
					req = req + convert(valueAttrib[i], nameAttrib[i], thisClass);
				else
					req = req + convert(valueAttrib[i], nameAttrib[i], thisClass) + ",";
			}
			req = req + ")";
		}
		return req;
	}

    public static boolean isValide(Object obj) {
		boolean ret = true;
		if (obj instanceof Number) {
			Number num = (Number) obj;
			if (num.intValue() == 0)
				return false;
			else if (num.doubleValue() == 0.0)
				return false;
			else
				return true;
		} else if (obj instanceof String) {
			String temp = (String) obj;
			if (temp.equalsIgnoreCase(""))
				return false;
			else
				return true;
		} else if (obj == null) {
			return false;
		}
		return ret;
	}

    public static String genCondition(Object filtre) throws Exception {
		String cond = "";
		Class theClass = filtre.getClass();
		String[] nameAttrib = getAttribName(theClass);
		Method[] lMethod = matchMethods(theClass, nameAttrib, "get");
		Method[] liste = order(nameAttrib, lMethod, "get");
		Object[] valueAttrib = callMethods(liste, filtre);
		int nbCond = 0;
		for (int i = 0; i < valueAttrib.length; i++) {
			if (isValide(valueAttrib[i])) {
				if (nbCond < 1)
					cond = cond + concat(valueAttrib[i], nameAttrib[i]);
				else
					cond = cond + "and " + concat(valueAttrib[i], nameAttrib[i]);
				nbCond++;
			}
		}
		String ret = "";
		if (cond.length() != 0)
			ret = ret + "where " + cond;
		return ret;
	}

	public static Object createInstance(Class objClass, HashMap<String, String> attribVal, String[] values){
    	Object instance = null;
    	try {
			instance = objClass.newInstance();
			String[] keys = (String[]) attribVal.keySet().toArray();
			Method[] lMethods = matchMethods(objClass, keys,"set");
			for(int i=0;i<keys.length;i++){
				if(attribVal.get(keys[i]).equals("int") || attribVal.get(keys[i]).equals("Integer")){
					lMethods[i].invoke(objClass, Integer.parseInt(attribVal.get(keys[i])));
				}
				else if (attribVal.get(keys[i]).equals("float") || attribVal.get(keys[i]).equals("Float")){
					lMethods[i].invoke(objClass, Float.parseFloat(attribVal.get(keys[i])));
				}
				else if (attribVal.get(keys[i]).equals("String")){
					lMethods[i].invoke(objClass, attribVal.get(keys[i]));
				}
				else if (attribVal.get(keys[i]).equals("Date") || attribVal.get(keys[i]).equals("Calendar")){
					int year = 0;
					int month = 0;
					int date = 0;
					String[] attrDate = new String[1];
					if(attribVal.get(keys[i]).contains("-")){
						attrDate = attribVal.get(keys[i]).split("-");
					}
					else if(attribVal.get(keys[i]).contains("/")) {
						attrDate = attribVal.get(keys[i]).split("/");
					}
					if (attrDate[0].length()==4){
						year = Integer.parseInt(attrDate[0]);
						month = Integer.parseInt(attrDate[1]);
						date = Integer.parseInt(attrDate[2]);
					}
					else{
						year = Integer.parseInt(attrDate[2]);
						month = Integer.parseInt(attrDate[1]);
						date = Integer.parseInt(attrDate[0]);
					}
					if(attribVal.get(keys[i]).equals("Date")){
						lMethods[i].invoke(instance, new Date(year, month, date));
					}
					else{
						Calendar cal = Calendar.getInstance();
						cal.set(year,month,date);
						lMethods[i].invoke(instance, cal);
					}
				}
			}
		}catch (Exception e){
    		e.printStackTrace();
		}
    	return instance;
	}
}
