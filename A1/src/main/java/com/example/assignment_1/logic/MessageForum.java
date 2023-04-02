package com.example.assignment_1.logic;

/**
 * The message forum is a singleton that represents the website as a whole. This stores the website, and any of the data
 * structures used. This singleton represents all the business logic of the website. All servlets attempting to access
 * data from the server should use this.
 */
public class MessageForum  {

    private final MessageBoard rootBoard = new MessageBoard();
    private static MessageForum instance;

    public MessageForum() {}

    public static MessageForum getInstance() {
        MessageForum singleton = instance;
        //thread safe double lock singleton
        if (singleton == null) {
            synchronized (MessageForum.class) {
                singleton = instance;
                if (singleton == null) {
                    instance = singleton = new MessageForum();
                }
            }
        }
        return singleton;
    }

    public MessageBoard getRootBoard() {
        return rootBoard;
    }
}
