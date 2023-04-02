package com.example.assignment_1.logic;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A message collection is any object that contains a list of messages. A message collection is inherently
 * a tree structure. Each message collection contains a list of messages, which itself is a message
 * collection which means each reply can be replied to.
 *
 * If a message collection does not have a parent, that is the parent is null, then it means that this is
 * the root message.
 */
public abstract class MessageCollection implements Iterable<Message> {

    /**
     * Thread safety should be maintained by using a message collection.
     */
    protected final List<Message> replies;
    protected final MessageCollection parent;

    /**
     * Creates a message collection.
     *
     * @param parent The parent message. Null if this is the root.
     */
    protected MessageCollection(MessageCollection parent) {
        this.parent = parent;
        //use the copy on write array list for thread safety, ensure future atomic operations are synchronised
        this.replies = new CopyOnWriteArrayList<>();
    }

    /**
     * Retrieves a reply based on the message Id.
     *
     * @param messageId the message id of the reply to get
     * @return The message with that message id or null if it cannot be found
     */
    public Message getReply(int messageId) {
        for(Message c : this) {
            //check if the message has the id
            if(c.getMessageId() == messageId)
                return c;

            //otherwise, check any replies the message might have
            Message m = c.getReply(messageId);
            if(m!=null)
                return m;
        }
        //return null if we cannot find a message
        return null;
    }

    /**
     * Adds a reply to this message using the message builder.
     *
     * @param messageCreator The message content
     * @return The newly created message
     * @throws IllegalArgumentException if a message cannot be added
     */
    public Message addReply(MessageCreator messageCreator) {
        if(!canAddReply())
            throw new IllegalArgumentException(moreRepliesAvailable().toString());

        Message message = createReply(messageCreator);
        replies.add(message);
        return message;
    }

    /**
     * Creates a new message reply for that message collection form.
     *
     * @param messageCreator The message content
     * @return The newly created message.
     * @throws IllegalArgumentException if the message creator does not conform to a new message's preconditions
     */
    protected abstract Message createReply(MessageCreator messageCreator);

    /**
     * @return Whether a new reply can be added to this message
     */
    public boolean canAddReply() {
        return moreRepliesAvailable().size()==0;
    }

    /**
     * @return A list of reasons as to why a reply cannot be added. If there are no reasons then a reply can be added.
     */
    protected abstract Collection<String> moreRepliesAvailable();

    /**
     * Returns a collection of all the replies to this message. If deep then it will list all the immediate replies
     * and not the replies of the replies. If deep is true then
     *
     * @param deep Whether to return replies of replies
     * @return All the replies to this message.
     */
    public Collection<Message> getReplies(boolean deep) {
        //if no deep just return all the immediate replies
        if(!deep) {
            return Collections.unmodifiableList(replies);
        } else {
            //get the deep replies
            List<Message> deepReplies = new ArrayList<>();
            getReplies(deepReplies);
            return Collections.unmodifiableCollection(deepReplies);
        }
    }

    /**
     * Helper function that retrieves the deep replies of this message. This will recursively add replies
     * over and over to the existing list.
     *
     * @param current The list of currently collected messages
     */
    protected void getReplies(List<Message> current) {
        for(Message message : this) {
            //loop through all the messages and add them.
            current.add(message);
            //add all the replies in this message.
            message.getReplies(current);
        }
    }

    @Override
    public Iterator<Message> iterator() {
        return replies.iterator();
    }

    @Override
    public Spliterator<Message> spliterator() {
        return replies.spliterator();
    }

    /**
     * Checks whether a reply of the given message number exists
     *
     * @param messageNumber The message number of the message
     * @return True if the reply exists and false if it does not
     */
    public boolean hasReply(int messageNumber) {
        return getReply(messageNumber)!=null;
    }

    @Override
    public String toString() {
        return replies.toString();
    }
}
