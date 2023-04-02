<%@ page import="com.example.assignment_2.game.SecreteNumberGame" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 24/03/2022
  Time: 1:17 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<%SecreteNumberGame game = (SecreteNumberGame) request.getAttribute("game");
String winStatement = game.playerWon() ? "You Won!" : "Game Over!";
%>
<html>
<head>
    <title><%=winStatement%></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h2><%=winStatement%></h2>
    <p class="info">Final Balance $<%=game.getPlayer().getMoney()%></p>
</body>
</html>
