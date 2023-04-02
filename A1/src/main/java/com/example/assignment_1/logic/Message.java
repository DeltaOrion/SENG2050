package com.example.assignment_1.logic;

import com.example.assignment_1.util.WFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A message represents a post to a message board. It is inherently a hierarchical rooted tree as message's have
 * the ability to be replied using another message.
 *
 * A message is composed at the top with the root
 *
 */
public class Message extends MessageCollection {

    private static int globalMessageId = 0;

    private final int messageId;
    private final long timePosted;
    private final String userName;
    private final String title;
    private final String content;
    private final MessageBoard messageBoard;
    private final Message root;

    /**
     * Creates a new message
     *
     * @param creator The content of the new message
     * @param messageBoard The message board which this message is posted to
     * @param parent The parent node. This may be the message board this is posted to.
     * @param root The root message. The highest parent message. It cannot be a reply.
     */
    protected Message(MessageCreator creator, MessageBoard messageBoard, MessageCollection parent, Message root) {
        super(parent);
        this.root = root;
        this.messageBoard = messageBoard;
        this.userName = makeUserName(creator.getUserName());
        this.title = validateContent(creator.getTitle(), this.messageBoard.settings().getMinTitleLength(), this.messageBoard.settings().getMaxTitleLength());
        this.content = validateContent(creator.getContent(), this.messageBoard.settings().getMinContentLength(), this.messageBoard.settings().getMaxContentLength());
        this.timePosted = creator.getPostTime();
        synchronized (Message.class) {
            this.messageId = ++globalMessageId;
        }
    }

    private String makeUserName(String userName) {
        //if the username is an empty string or null then return the default name
        if(userName==null)
            return messageBoard.settings().getDefaultName();

        if(userName.length() == 0)
            return messageBoard.settings().getDefaultName();

        //return the entered username
        return userName;
    }

    /**
     * Validates a string entry on its preconditions. A string entry must at least minSize characters
     * and at most maxSize characters. If minSize is -1 then it can have as little characters as it needs
     * and if maxSize is -1 then it can have as many as it likes. A message may not be null.
     *
     * @param content The entry to be validated
     * @param minSize The minimum size of the entry
     * @param maxSize The maximum size of the entry.
     * @return The original content
     * @throws IllegalArgumentException if the message does not meet the preconditions.
     */
    private String validateContent(String content, int minSize, int maxSize) {
        if(content==null)
            throw new IllegalArgumentException("Content cannot be unspecified");

        if(maxSize==-1)
            maxSize = Integer.MAX_VALUE;

        if(content.length()>=minSize && content.length()<=maxSize)
            return content;

        throw new IllegalArgumentException("Content must be greater than '"+minSize+"' or less than '"+maxSize+"'");
    }

    /**
     * @return A username that can be safely displayed in HTML
     */
    public String getFormattedUserName() {
        return format(userName);
    }

    /**
     * @return a title that can safely be displayed in HTML
     */
    public String getFormattedTitle() {
        return format(title);
    }

    /**
     * @return content that can be safely be displayed in HTML
     */
    public String getFormattedContent() {
        return format(content);
    }

    /**
     * @return Universal Time representation of when the content was posted.
     */
    public long getTimePosted() {
        return timePosted;
    }

    /**
     * @param dtf The datetime format that works with the {@link SimpleDateFormat}. The date's will be displayed in server
     *            time and not in localized time.
     * @return A string format of the date the message was posted.
     */
    public String getDatePosted(String dtf) {
        SimpleDateFormat sdf = new SimpleDateFormat(dtf);
        Date resultDate = new Date(getTimePosted());
        return sdf.format(resultDate);
    }

    private String format(String str) {
        if(!messageBoard.settings().isSanitizeAllMessages())
            return str;
        //sanitizes the message to stop XXS
        return WFormatter.escapeHTML(str);
    }


    @Override
    protected Message createReply(MessageCreator messageCreator) {
        //create a message reply with this being the parent. If this is the root specify that.
        return new Message(messageCreator,messageBoard,this,root == null ? this : root);
    }

    @Override
    protected Collection<String> moreRepliesAvailable() {
        List<String> reasons = new ArrayList<>();
        if(getRepliesCount()>=messageBoard.settings().getMaxReplies())
            reasons.add("Too many messages. Only '"+messageBoard.settings().getMaxReplies()+"' are allowed");

        return reasons;
    }

    public int getMessageId() {
        return messageId;
    }

    protected int getRepliesCount() {
        //helper method to get the number of root replies to this message.
        if(root==null) {
            //return the amount of deep replies if this is root
            return getReplies(true).size();
        } else {
            //return the amount of root replies.
            return root.getRepliesCount();
        }
    }

    public String toString() {
        return new StringBuilder("Message {")
                .append("User: ").append(userName).append(", ")
                .append("Title: ").append(title).append(", ")
                .append("Content: ").append(content).append(", ")
                .append("id: ").append(messageId).append(", ")
                .append("}")
                .toString();
    }

    public boolean isReply() {
        return root!=null;
    }

}
