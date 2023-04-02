package com.example.assignment_2.game;

import java.io.Serializable;
import java.util.*;

/**
 * This is a java bean that represents the secret number game described in the brief.
 * This class holds the state of the game and provides methods to change the state of the game in accordance with the rules.
 * This bean acts as both an entity, holding the state of the game and a controller providing the mechanisms to legally
 * change its state.
 */
public class SecreteNumberGame implements Serializable {

    private final transient Random random;
    private int secretNumber;
    private int round;
    private final Set<Integer> revealedNumbers;
    private final Player player;
    private final Settings settings;
    private GameState state;
    private boolean gameWon;

    private static final long serialVersionUID = -5064957709895713381L;

    /**
     * Creates a new game with the specified player. The game will not be
     * started. It needs to be started with {@link #start()}
     *
     * @param player The player sho is playing the game.
     */
    public SecreteNumberGame(Player player) {
        settings = new Settings();
        this.random = new Random(System.currentTimeMillis());
        revealedNumbers = new HashSet<>();
        this.player = player;
        state = GameState.WAITING;
    }

    /**
     * Creates a new game with the default {@link Player}
     */
    public SecreteNumberGame() {
        this(new Player());
    }

    /**
     * Starts the game. Before the game is started its state cannot be legally modified. Once the game has been started it will
     * begin accepting guesses. Guesses should be submitted to the game {@link #submitGuesses(Collection)}
     *
     */
    public void start() {
        //we cant start the game if it has already started
        if(state!=GameState.WAITING)
            throw new IllegalStateException("Game has already started");
        
        synchronized (this) {
            reset();
            state = GameState.AWAITING_GUESS;
        }
    }

    /**
     * Allows guesses to be submitted to the game. The game must be accepting guesses in the state {@link GameState#AWAITING_GUESS}. The game
     * will then ccheck the guesses and add them to the revealed numbers. If one of the numbers is the {@link #getSecretNumber()} the game will be terminated
     * and moved to the state {@link GameState#FINISH}. If none of the guesses are the {@link #getSecretNumber()} and the round is not the max round
     * then the game will move onto the {@link GameState#AWAITING_GUESS}. Guesses should then be submitted with {@link #submitGuesses(Collection)}
     *
     *The guesses must be between valid in accordance with {@link #validateGuesses(Collection)}
     * 
     * @param guesses A set of guesses to submit 
     * @throws IllegalStateException if the program is not awaiting guesses
     * @throws IllegalArgumentException if the guesses do not conform to {@link #validateGuesses(Collection)}
     */
    public void submitGuesses(Collection<Integer> guesses) {
        if(state != GameState.AWAITING_GUESS)
            throw new IllegalStateException("Attempted to submit guesses when the game is not listening for guesses");

        if(guesses==null)
            throw new IllegalArgumentException("You cannot submit no guesses");

        //check the guesses
        validateGuesses(guesses);

        boolean foundNumber = false;
        //atomic modification of shared state so it must be synchronized
        synchronized (this) {
            //add gueses to the revealed numbers and see if one is the secret number
            for (int guess : guesses) {
                revealedNumbers.add(guess);
                if (guess == secretNumber)
                    foundNumber = true;
            }

            //if we found the number terminate the game
            if (foundNumber) {
                terminateGame();
                return;
            }

            //if we reached the end then we won the game
            if (round == settings.getMaxRounds()) {
                winGame();
                return;
            }
            //start accepting offers
            state = GameState.AWAITING_OFFER;
        }
    }

    /**
     * Allows the end of round offer to be submitted. The amount this offer is determined by {@link #getRoundEndPrize()}. If the state
     *
     *  - {@link RoundEndOffer#ACCEPT} the game will terminate and the player gets the round end prize
     *  - {@link RoundEndOffer#DENY} the game will continue to the next round and they will not get the end prize
     * 
     * @param offer The offer sent to the game
     */
    public void submitRoundEndOffer(RoundEndOffer offer) {
        if(state!=GameState.AWAITING_OFFER)
            throw new IllegalStateException("Not accepting ending offer");

        synchronized (this) {
            if (offer == RoundEndOffer.ACCEPT)
                acceptEndRoundOffer();

            if (offer == RoundEndOffer.DENY)
                denyEndRoundOffer();
        }
    }

    /**
     * Performs the logic of accepting the round end offer
     *   - adds the round end prize
     *   - terminates the game
     */
    private synchronized void acceptEndRoundOffer() {
        player.setMoney(player.getMoney() + getRoundEndPrize());
        terminateGame();
    }

    /**
     * Performs the logic of denying the round offer
     *   - goes to next round
     *   - starts listening for guesses
     */
    private synchronized void denyEndRoundOffer() {
        round++;
        state = GameState.AWAITING_GUESS;
    }

    /**
     * Performs game win logic
     *   - give player {@link #getGameEndPrize()}
     *   - set the game to win
     *
     */
    private synchronized void winGame() {
        player.setMoney(player.getMoney() + getGameEndPrize());
        gameWon = true;
        state = GameState.FINISH;
    }

    /**
     * Checks that the guesses are correct and can be submitted
     *   - it must have in total {@link #getGuesses()} numbers
     *   - none of the guesses may exist in the guessed numbers
     *   - none of the guesses can be the same
     *   - all guesses must conform to {@link #isGuessValid(int)}
     *
     * @param guesses The guesses to validate
     * @throws IllegalArgumentException if the guesses are not valid
     */
    private void validateGuesses(Collection<Integer> guesses) {
        int requiredGuesses = getGuesses();
        //check there are the correct amount of guesses in the list
        if(guesses.size()!=requiredGuesses)
            throw new IllegalArgumentException("More guesses submitted than the allowed '"+requiredGuesses+"'");

        //check that none of the guesses are the same and that all of the guesses are valid
        Set<Integer> check = new HashSet<>();
        for(int guess : guesses) {
            if(!isGuessValid(guess))
                throw new IllegalArgumentException(invalidGuessReasons(guess).toString());

            if(check.contains(guess))
                throw new IllegalArgumentException("Duplicate guess in the round '"+guess+"'");

            check.add(guess);
        }
    }

    /**
     * Checks if an individual guess is valid
     * 
     * @param guess the guess number to validate
     * @return true if the guess is valid and false if it is not
     */
    private boolean isGuessValid(int guess) {
        return invalidGuessReasons(guess).size()==0;
    }

    /**
     * Checks if a guess is valid and provides reasons if it is not. The reasons are stored in a collection of strings and returned
     * If there are no reasons then the guess is valid.
     *
     * @param guess The guess to be checked
     * @return A list of reasons as to why the guess is correct
     */
    private Collection<String> invalidGuessReasons(int guess) {
        List<String> reasons = new ArrayList<>();
        //check the guesses is in range
        if(!(guess >= settings.getMinGuess() && guess <= settings.getMaxGuess())) {
            reasons.add("The guess '"+guess+"' is invalid as it is not between '"+ settings().getMinGuess()+"' and '"+ settings().getMaxGuess()+"'");
        }

        //check that the guess has not already been revealed
        if(getRevealedNumbers().contains(guess)) {
            reasons.add("That guess '"+guess+"' has already been made");
        }
        return Collections.unmodifiableList(reasons);
    }

    /**
     * Does game defeat logic
     *
     */
    private synchronized void terminateGame() {
        state = GameState.FINISH;
    }

    /**
     * Returns whether the player has won the game
     *
     * @return true if the game has been won and false if not
     */
    public boolean playerWon() {
        return state==GameState.FINISH && gameWon;
    }

    /**
     * Logic to reset the game
     */
    private synchronized void reset() {
        round = 1;
        secretNumber = (generateNumber(settings().getMinGuess(), settings().getMaxGuess()));
        revealedNumbers.clear();
        gameWon = false;
    }

    /**
     * Generates a new random number between the minimum value and the maximum value inclusively.
     *
     * @param min The min value to generate from
     * @param max The highest value to generate to
     * @return A new random number between
     */
    private int generateNumber(int min, int max) {
        return random.nextInt((max+1) - min) + min;
    }


    /**
     * Returns the maximum amount of allowed guesses for the round. The round guess function
     * is determined by the current round.
     *
     * @return The maximum number of guesses in the round.
     */
    public synchronized int getGuesses() {
        return this.round;
    }

    /**
     * The amount of money received is determined by the prize multiplier which is $100 * the secrete number.
     *
     * @return The amount of money received if the user wins the game
     */
    public int getGameEndPrize() {
        int secretNumber;
        synchronized (this) {
            secretNumber = this.secretNumber;
        }
        return settings().getPrizeMultiplier() * secretNumber;
    }

    /**
     * The amount of money received at the end of the round is determined by the minimum unrevealed number
     *  * 100.
     *
     * @return The money received at the round end offer.
     */
    public int getRoundEndPrize() {
        Collection<Integer> revealedNumbers = getRevealedNumbers();
        //retrieve the lowest unrevealed number
        for(int i=settings().getMinGuess();i<=settings().getMaxGuess();i++) {
            if(!revealedNumbers.contains(i)) {
                return i*settings().getPrizeMultiplier();
            }
        }
        //return the lowest unrevealed number * the multiplier
        return settings.getPrizeMultiplier() * settings.getMaxGuess();
    }

    /**
     *
     * Returns a collection containing all the number revealed by the player so far. This cannot be modified directly and
     * instead should be modified
     *
     * @return The collection of revealed numbers so far.
     */
    public Collection<Integer> getRevealedNumbers() {
        //thread safety achieved by immutability
        return Collections.unmodifiableSet(revealedNumbers);
    }

    public Settings settings() {
        return settings;
    }

    public synchronized int getRound() {
        return round;
    }

    public synchronized GameState getState() {
        return state;
    }

    public Player getPlayer() {
        return player;
    }

    public synchronized int getSecretNumber() {
        return secretNumber;
    }

    /**
     * Settings class provides the settings for the game such as the total number of rounds and the minimum/maximum guesses. These can be
     * configured to quickly change how the game functions.
     */
    public static class Settings implements Serializable {
        private int maxRounds;
        private int prizeMultiplier;
        private int minGuess = 1;
        private int maxGuess = 11;

        public Settings() {
            maxRounds = 4;
            prizeMultiplier = 100;
        }

        public int getMaxRounds() {
            return maxRounds;
        }

        public Settings setMaxRounds(int maxRounds) {
            this.maxRounds = maxRounds;
            return this;
        }
        public int getPrizeMultiplier() {
            return prizeMultiplier;
        }

        public Settings setPrizeMultiplier(int prizeMultiplier) {
            this.prizeMultiplier = prizeMultiplier;
            return this;
        }

        public int getMinGuess() {
            return minGuess;
        }

        public Settings setMinGuess(int minGuess) {
            this.minGuess = minGuess;
            return this;
        }

        public int getMaxGuess() {
            return maxGuess;
        }

        public Settings setMaxGuess(int maxGuess) {
            this.maxGuess = maxGuess;
            return this;
        }
    }
}
