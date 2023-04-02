<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 30/03/2022
  Time: 12:38 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="/style.css">
    <title>Save Game</title>
</head>
<body>
    <h1>Save Game?</h1>
    <p>Saving will return to save screen</p>

    <h2>Save Game</h2>
    <form name="save-accept" method="get" action="gameRequest">
        <input type="hidden" name="type" value="save">
        <input type="submit" value="Save">
    </form>
    <hr/>
    <h2>Do not save game</h2>
    <form name="save-deny" method="get" action="gameRequest">
        <input type="hidden" name="type"  value="show">
        <input type="hidden" name="accept" value="false">
        <input type="submit" value="Continue">
    </form>
</body>
</html>
