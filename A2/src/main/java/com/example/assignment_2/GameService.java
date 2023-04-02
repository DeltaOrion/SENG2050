package com.example.assignment_2;

import com.example.assignment_2.game.Player;
import com.example.assignment_2.game.RoundEndOffer;
import com.example.assignment_2.game.SecreteNumberGame;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet which acts as a boundary and a control class for interacting with the game. This servlet
 * takes in various requests from the game, processes them and produces the appropriate output for
 * the servlet game. All output will be displayed by view classes or jsps.
 *
 * The sent URL should always contain the paramter "type". The type must be a valid action shown from {@link GameAction}. Each
 * game action represents an action for a game such as submitting a guess or entering a round end offer. The request should also
 * contain additional information.
 *
 * View information about each request in {@link GameAction}
 *
 * This is probably not the ideal way of doing things as the web requests are coupled with the controller "commands" but I
 * did it this way for simplicity
 *
 */
@WebServlet(name = "gameServlet", value = "/gameRequest")
public class GameService extends HttpServlet {

    private final GameManager saveManager = new GameManager();


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String type = request.getParameter("type");
        //check the request is valid
        if(type==null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No game request found!");
            return;
        }

        //retrieve the action from the request
        GameAction action = GameAction.fromToken(type);
        if(action==null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Game Action!");
            return;
        }

        //run the appropiate action from the given request
        switch (action) {
            case CONTINUE:
                continueGame(request,response);
                break;
            case START_NEW:
                startNewGame(request,response);
                break;
            case SUBMIT_GUESS:
                submitGuess(request,response);
                break;
            case SUBMIT_OFFER:
                submitOffer(request,response);
                break;
            case SAVE_GAME:
                saveGame(request,response);
                break;
            case SHOW:
                showGame(request,response);
                break;
            default:
                //send an error if there is nothing linked to that action then something has gone terribly wrong.
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No specified behavior for the action '"+action+"'");
                break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request,response);
    }

    /**
     * Shows the appropriate page for the game. It retrieves the correct page to show for a games state. For example if the game
     * is currently awaiting a guess, this shows the awaiting guess page.
     *
     */
    private void showPageForGame(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game) throws ServletException, IOException {
        switch (game.getState()) {
            case AWAITING_GUESS:
                showGuessPage(request,response,game);
                break;
            case FINISH:
                showWinPage(request,response,game);
                break;
            case AWAITING_OFFER:
                showOfferPage(request,response,game);
                break;
            case WAITING:
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No page for waiting state");
                break;
        }
    }

    /**
     *  Logic for the {@link GameAction#SHOW} request. Check that the user has a game and if they do show the correct
     *  page for that game state using {@link #showPageForGame(HttpServletRequest, HttpServletResponse, SecreteNumberGame)}
     */
    private void showGame(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SecreteNumberGame game = saveManager.getGameFromSession(request.getSession());
        //check user is not logged on
        if(game==null) {
            notLoggedIn(response,request);
            return;
        }
        //otherwise, show the page
        showPageForGame(request,response,game);
    }

    /**
     *  Logic for the {@link GameAction#SUBMIT_OFFER}. Convert the offer to a {@link com.example.assignment_2.game.SecreteNumberGame.RoundEndOffer}
     *
     *  Paramters:
     *    "accept": true = accept offer    false = deny offer
     *
     */
    private void submitOffer(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        SecreteNumberGame game = saveManager.getGameFromSession(request.getSession());
        if(game==null) {
            notLoggedIn(response,request);
            return;
        }
        //verify the parameter is present
        if(request.getParameter("accept")==null) {
            response.sendError(400, "Request did not specify to accept ");
            return;
        }

        //convert the string to a round accept offer
        RoundEndOffer offer;
        String acceptance = request.getParameter("accept");
        if(acceptance.equals("true")) {
            offer = RoundEndOffer.ACCEPT;
        } else if(acceptance.equals("false")) {
            offer = RoundEndOffer.DENY;
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unknown Offer Acceptance '"+acceptance+"'");
            return;
        }

        //submit the round end offer
        try {
            game.submitRoundEndOffer(offer);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            return;
        } catch (IllegalStateException e) {
            showPageForGame(request,response,game);
            return;
        }

        if(!saveManager.contains(game.getPlayer().getUsername())) {
            showSavePage(request, response, game);
        } else {
            showPageForGame(request,response,game);
        }
    }

    /**
     * Shows a page for the given game
     *
     * @param game The game to show the page for
     * @param JSP The name of the file to pull
     */
    private void showPage(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game, String JSP) throws ServletException, IOException {
        request.setAttribute("game",game);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/"+JSP+".jsp");
        dispatcher.forward(request,response);
    }


    private void showSavePage(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game) throws ServletException, IOException {
        showPage(request,response,game,"GameSave");
    }

    private void showOfferPage(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game) throws ServletException, IOException {
        showPage(request,response,game,"GameOffer");
    }

    private void showWinPage(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game) throws ServletException, IOException {
        showPage(request,response,game,"GameOver");
    }

    /**
     * Represents logic for the {@link GameAction#SUBMIT_GUESS} state
     *
     * Parameters:
     *   Guess0: first guess
     *   Guess1: second guess
     *    ..............
     *   GuessN: nth guess
     *
     */
    private void submitGuess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecreteNumberGame game = saveManager.getGameFromSession(request.getSession());
        if(game==null) {
            notLoggedIn(response,request);
            return;
        }

        //convert guesses into list
        List<Integer> guesses = new ArrayList<>();
        try {
            for (int i = 0; i < game.getGuesses(); i++) {
                guesses.add(i, Integer.parseInt(request.getParameter("guess" + i)));
            }
        } catch (NumberFormatException e) {
            //they are probably being malicious if this happens.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            return;
        }

        //submit the guesses
        try {
            game.submitGuesses(guesses);
        } catch (IllegalArgumentException e) {
            //showGuessPage(request,response,game);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            return;
        } catch (IllegalStateException e) {
            //show appropiate page if not allowing guesses
            showPageForGame(request,response,game);
            return;
        }

        showPageForGame(request,response,game);
    }

    private void notLoggedIn(HttpServletResponse response, HttpServletRequest request) throws IOException {
        sendHome(request,response);
    }

    private void sendHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath());
    }

    /**
     * Creates a new game
     * Parameters:
     *   username: the persons username
     *
     */
    private void startNewGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        //check username
        if(!Player.validUsername(username)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No username specified!");
            return;
        }
        //if username good make a new game
        SecreteNumberGame game = new SecreteNumberGame(new Player(username));
        saveManager.setSessionGame(request.getSession(),game);

        //if game exists make a new one
        if(saveManager.contains(username)) {
            saveManager.saveGame(game);
        }
        game.start();
        showPageForGame(request,response,game);
    }

    private void showGuessPage(HttpServletRequest request, HttpServletResponse response, SecreteNumberGame game) throws ServletException, IOException {
        request.setAttribute("game",game);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/GameGuess.jsp");
        dispatcher.forward(request,response);
    }

    /**
     * continues a game
     *  Parameters:
     *    usrname: The players username
     *
     */
    private void continueGame(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("usrname");
        if(!Player.validUsername(username)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No username specified!");
            return;
        }
        //get the games player
        SecreteNumberGame game = saveManager.loadGame(username);
        if(game==null) {
            //if it does not exist display page showing this
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/CantFindGame.jsp");
            request.setAttribute("save-name",username);
            dispatcher.forward(request,response);
            return;
        }

        //otherwise, set the game session
        saveManager.setSessionGame(request.getSession(),game);
        showPageForGame(request,response,game);
    }

    private void saveGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecreteNumberGame game = saveManager.getGameFromSession(request.getSession());
        if(game==null) {
            notLoggedIn(response,request);
            return;
        }

        saveManager.saveGame(game);
        sendHome(request,response);
    }

    @Override
    public void destroy() {
        try {
            saveManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            saveManager.init(Paths.get(getServletContext().getRealPath("/WEB-INF")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Represents an action that can be done by a given request
     *
     *  - {@link #START_NEW} starts a new game. Parameters: "username" -  the players username
     *  - {@link #CONTINUE} continues an existing game. Parameters: "usrname" - the player username
     *  - {@link #SUBMIT_GUESS} submits guesses. Parameters: "guess0 - the first guess, guess1 - the second guess, .... , guessN the nth guess"
     *  - {@link #SUBMIT_OFFER} submits a round end offer. Parameters: "true" - accepts offer, "false" - denies offer
     *  - {@link #SAVE_GAME} saves the game.
     *  - {@link #SHOW} Shows the correct current state of the game.
     */
    private enum GameAction {
        START_NEW("new"),
        CONTINUE("continue"),
        SUBMIT_GUESS("guess"),
        SUBMIT_OFFER("offer"),
        SAVE_GAME("save"),
        SHOW("show"),
        ;
        //the a string used to identify the action
        private final String token;

        GameAction(String token) {
            this.token = token;
        }

        /**
         * Returns the appropriate action from the string and null if there is none.
         *
         * @param action The string form of the action.
         * @return The linked game action to that string.
         */
        public static GameAction fromToken(String action) {
            for(GameAction act : values()) {
                if(act.token.equals(action))
                    return act;
            }

            return null;
        }

    }
}
