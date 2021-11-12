<%@ page import="java.util.HashMap" %>
<%@ page import="com.toavina.WebFramework.model.Employer" %><%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 24/08/2021
  Time: 14:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object[] listEmp = (Object[]) application.getAttribute("empList");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
  <% for(Object obj: listEmp){ %>
        <% Employer emp = (Employer) obj; %>
        <p><%= emp.getNom()%></p>
        <p><%= emp.getPrenom()%></p>
    <% } %>
</body>
</html>
