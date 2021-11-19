<%@ page import="com.toavina.WebFramework.model.Employer" %><%--
  Created by IntelliJ IDEA.
  User: toavi
  Date: 19/11/2021
  Time: 15:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Object[] emp = (Object[]) application.getAttribute("empList");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
  <% for(Object temp: emp){ %>
  <% Employer e = (Employer) temp; %>
    <div>
      <p>Nom: <%= e.getNom()%></p>
      <p>Nom: <%= e.getPrenom()%></p>
    </div>
  <% } %>
</body>
</html>
