<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 24/08/2021
  Time: 14:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String text = (String) application.getAttribute("SayHiValue");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
  <p><%= text %></p>
</body>
</html>
