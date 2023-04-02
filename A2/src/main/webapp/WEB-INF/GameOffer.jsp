<%@ page import="com.example.assignment_2.game.SecreteNumberGame" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 24/03/2022
  Time: 1:13 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<%SecreteNumberGame game = (SecreteNumberGame) request.getAttribute("game");%>
<html>
<head>
    <title>Round End Offer</title>
    <link rel="stylesheet" href="style.css">
    <script src="validation.js" type="text/javascript"></script>
</head>
<body>
<h1>End of Round Offer</h1>
<p class="info">The game is offering $<%=game.getRoundEndPrize()%></p>
<p class="info">Revealed Numbers - <%=game.getRevealedNumbers()%></p>
<p class="info">Current Balance: $<%=game.getPlayer().getMoney()%></p>
<h2>Accept Offer (Ends Game)</h2>
<form name="offer-accept" method="get" action="gameRequest">
    <input type="hidden" name="type" value="offer">
    <input type="hidden" name="accept" value="true">
    <input type="submit" value="Accept">
</form>
<hr/>
<h2>Decline Offer (Continues)</h2>
<form name="offer-deny" method="get" action="gameRequest">
    <input type="hidden" name="type"  value="offer">
    <input type="hidden" name="accept" value="false">
    <input type="submit" value="Continue">
</form>
</body>
</html>
