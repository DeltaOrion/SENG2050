package com.example.assignment_1.logic;

/**
 * A message creator represents potential content for a new message. As new parameters may be added at any time to a message
 * standard constructor cannot be used. This also allows the message constructor to meet is post-conditions better.
 *
 * This class acts almost like a builder except it is read by the relevant message collection. This means the creator
 * uses aggregation rather than composition.
 */
public class MessageCreator {
    private final String title;
    private final String content;
    private long postTime = System.currentTimeMillis();
    private String userName;

    public MessageCreator(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public MessageCreator setPostTime(long postTime) {
        this.postTime = postTime;
        return this;
    }

    public MessageCreator setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getPostTime() {
        return postTime;
    }

    public String getUserName() {
        return userName;
    }
}
