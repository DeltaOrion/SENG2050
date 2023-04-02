<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 24/03/2022
  Time: 11:49 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Game Start</title>
    <link rel="stylesheet" href="style.css">
    <script src="validation.js" type="text/javascript"></script>
    <script>
        const startForm = {
            name: "game-start",

            //the actual html element id for each field
            fields: {
                username: "username",
            },

            validations: {
                username: {
                    required: true,
                    minLength: 1,
                },
            },
        }

        const continueForm = {
            name: "game-continue",

            //the actual html element id for each field
            fields: {
                username: "username",
            },

            validations: {
                username: {
                    required: true,
                    minLength: 1,
                },
            },
        }
    </script>
</head>
<body>
    <h1>Avoid the SECRETE Number!!!</h1>
    <h2>New Game</h2>
    <form name="game-start" method="get" action="gameRequest" onsubmit="return handleSubmit(startForm)">
        <input type="hidden" name="type" value="new">
        <label id="username_err" class="err"></label><br/>
        <label for="username-start">Username: </label><input type="text" name="username" id="username-start"><br>
        <input type="submit" value="New Game">
    </form>
    <hr/>
    <h2>Continue Game</h2>
    <h3>If a game isn't found this starts a new save game</h3>
    <form name="game-continue" method="get" action="gameRequest" onsubmit="return handleSubmit(continueForm)">
        <input type="hidden" name="type"  value="continue">
        <label id="usrname_err" class="err"></label><br/>
        <label for="usrname">Username: </label><input type="text" name="usrname" id="usrname"><br>
        <input type="submit" value="Continue">
    </form>
</body>
</html>
