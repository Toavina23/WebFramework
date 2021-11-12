<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>

<form action="${pageContext.request.contextPath}/request/put/test-ajouter" method="post">
    <input type="text" name="test.nom">
    <input type="text" name="test.prenom">
    <input type="text" name="test.age">
    <button type="submit">Valider</button>
</form>

<a href="${pageContext.request.contextPath}/request/employer-testAnnotation">
    <button>Teste d'acces par annotation</button>
</a>

</body>
</html>