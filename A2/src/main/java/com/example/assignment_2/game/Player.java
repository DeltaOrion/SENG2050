package com.example.assignment_2.game;

import java.io.Serializable;

/**
 * Represents a person who plays the {@link SecreteNumberGame}
 */
public class Player implements Serializable {

    private String username;
    private int money;

    private static final long serialVersionUID = 6882584293739123760L;

    public Player() {
        this.money = 0;
        this.username = null;
    }

    /**
     * Creates a player with the given valid username.
     *
     * @param username the players username.
     * @throws IllegalArgumentException if the username is not valid
     */
    public Player(String username) {
        if(!validUsername(username))
            throw new IllegalArgumentException("Bad Username '"+username+"'");

        this.money = 0;
        this.username = username;
    }

    /**
     * Sets the players money to the given value
     *
     * @param money the new money
     */
    public synchronized void setMoney(int money) {
        this.money = money;
    }

    /**
     * @return The players username
     */
    public synchronized String getUsername() {
        return username;
    }

    /**
     * Sets the player username. It must comply with {@link #validUsername(String)}
     *
     * @param username the new username.
     */
    public void setUsername(String username) {
        if(!validUsername(username))
            throw new IllegalArgumentException("Cannot have no username!");
        synchronized (this) {
            this.username = username;
        }
    }

    /**
     * @return the player's money.
     */
    public synchronized int getMoney() {
        return money;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Player))
            return false;

        Player player = (Player) o;
        return player.getUsername().equals(this.getUsername());
    }

    /**
     * A valid username is a non-null username with a length greater than 0.
     *
     * @param username The username to check
     * @return if the usernam is valid
     */
    public static boolean validUsername(String username) {
        if(username==null)
            return false;

        return username.length() > 0;
    }
}
