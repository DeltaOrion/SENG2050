<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 7/04/2022
  Time: 2:14 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cant Find Game :(</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>We looked far and wide</h1>
    <p class="info">But we could not find a save game with the name <%=request.getAttribute("save-name")%></p>
</body>
</html>
