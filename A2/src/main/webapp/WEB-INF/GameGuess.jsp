<%@ page import="com.example.assignment_2.game.SecreteNumberGame" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 24/03/2022
  Time: 12:23 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <%SecreteNumberGame game = (SecreteNumberGame) request.getAttribute("game");%>
    <title>Make Guess</title>
    <link rel="stylesheet" href="style.css">
    <script src="validation.js" type="text/javascript"></script>
    <script>
        const guessForm = {
            //automatically generate json that is used by validation.js
            name: "game-guess",

            //the actual html element id for each field
            fields: {
                //generate javascript validation for this form
                <%
                    for(int i=0;i<game.getGuesses();i++) {
                        String name = "guess" + i;

                %>
                <%=name + ": \"" + name + "\","%>
                <%}%>
            },
            //validation requirements for each field
            validations: {
                <%
                    for(int i=0;i< game.getGuesses();i++) {
                        String name = "guess" + i;

                %>
                <%=name + ": {"%>
                <%="required: true,"%>
                <%="numeric: true,"%>
                <%="minNumericValue: " + game.settings().getMinGuess() + ","%>
                <%="maxNumericValue: "+ game.settings().getMaxGuess() + ","%>
                <%
                    //create list of numbers to exclude for the JS validation
                    StringBuilder exclude = new StringBuilder();
                    int count = 0;
                    int size = game.getRevealedNumbers().size();
                    for(int num : game.getRevealedNumbers()) {
                        exclude.append(num);
                        if(count<size-1)
                            exclude.append(", ");
                        count++;
                    }
                %>
                <%="exclude: [" + exclude + "]," %>
                <%="},"%>
                <%}%>
            }
        }

        function checkGuesses(formCriteria) {
            //this code is kinda eh and speghetti but its wont be reused so it isnt that big a deal.
            //this function checks that none of the guesses are duplicate. If they are the form will not be submitted
            const form = document.forms[formCriteria.name];
            //check that none of the submissions are equal
            let errors = {};
            for(const key in formCriteria.fields) {
                errors[key] = [];
            }

            //loop
            for (const key in formCriteria.fields) {
                const i = form[formCriteria.fields[key]].value;
                for(const key1 in formCriteria.fields) {
                    const j = form[formCriteria.fields[key1]].value;
                    if(i==j && key!==key1) {
                        const err = "duplicate guess";
                        errors[key].push(err);
                        errors[key1].push(err);
                        console.log(errors);
                        printErrors(formCriteria,errors);
                        return false;
                    }

                }
            }
            return true;
        }

        //handle extra form validation logic, i.e check that none of the numbers are duplicate and the rest of the form validation.
        function handleGuessSubmit(formCriteria) {
            if(!handleSubmit(formCriteria))
                return false;

            return checkGuesses(formCriteria);
        }
    </script>
</head>
<body>
    <h1>Guess the Number</h1>
    <p>CHEAT MODE ACTIVE</p>
    <p> Secret Number = <%= game.getSecretNumber()%></p>
    <p>Round <%=game.getRound()%></p>
    <hr/>
    <form name="game-guess" method="get" onsubmit="return handleGuessSubmit(guessForm)" action="gameRequest">
        <input type="hidden" name="type"  value="guess">
        <p>Make Guesses: </p>
        <%
            //make all guess fields
            for(int i=0;i<game.getGuesses();i++) {
                String name = "guess"+i;
        %>
            <label class="err" id="<%=name+"_err"%>">&nbsp;</label><br/>
            <input type="text" name="<%=name%>" id="<%=name%>"><br/>
        <%}%>
        <input type="submit" value="make guesses">
    </form>
</body>
</html>
