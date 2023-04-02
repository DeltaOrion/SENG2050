package com.example.assignment_2.game;

/**
 * Represents the state of the {@link SecreteNumberGame}. The states are as follows
 *
 *   - {@link #WAITING} The game is not started. the internal state of the game cannot be mutated
 *   - {@link #AWAITING_GUESS} The game has started and is waiting a guess
 *   - {@link #AWAITING_OFFER} The game has started and is waiting for an offer to be submitted
 *   - {@link #FINISH} The game has finished. The player has won or lost. The internal state of the game cannot be mutated.
 */
public enum GameState {
    WAITING,
    AWAITING_GUESS,
    AWAITING_OFFER,
    FINISH;
}
