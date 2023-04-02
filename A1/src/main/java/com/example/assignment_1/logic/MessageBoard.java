package com.example.assignment_1.logic;

import java.util.Collection;
import java.util.Collections;

/**
 * A message board is a message collection that represents an online discussion board which stores messages.
 * Any actor can post a message or thread to the board which may contain any topic of their liking.
 * Any actor can then post a reply to that message and a reply to the reply and so forth.
 *
 * The message board is also configurable and the preconditions for changing a message posted can change.
 */
public class MessageBoard extends MessageCollection {

    private final Settings settings;

    public MessageBoard() {
        super(null);
        this.settings = new Settings();
    }

    /**
     * @return The preconditions for posting a message to the board.
     */
    public Settings settings() {
        return settings;
    }


    @Override
    protected Message createReply(MessageCreator messageCreator) {
        //make a root message with this board being its parent.
        return new Message(messageCreator,this,this,null);
    }

    @Override
    protected Collection<String> moreRepliesAvailable() {
        return Collections.emptyList();
    }

    /**
     * Settings for any message posted to this board. This specifies parameters such as the content length,
     * max replies or the default name. All setter methods can be chained.
     *
     * For numeric settings if it is set to -1 it usually means the setting is 'off' or should not be applied
     */
    public static class Settings {

        private String defaultName = "Anonymous";
        private int maxReplies = 10;
        private int maxContentLength = 50;
        private int minContentLength = 5;

        private int minTitleLength = 1;
        private int maxTitleLength = -1;

        private boolean sanitizeAllMessages = true;

        /**
         * @return The name to be used if the user does not provide a name
         */
        public String getDefaultName() {
            return defaultName;
        }

        public Settings setDefaultName(String defaultName) {
            this.defaultName = defaultName;
            return this;
        }

        /**
         * @return The maximum total replies to a message. This counts all messages from the root.
         */
        public int getMaxReplies() {
            return maxReplies;
        }

        public Settings setMaxReplies(int maxReplies) {
            this.maxReplies = maxReplies;
            return this;
        }

        /**
         * @return the maximum length of any posted content
         */
        public int getMaxContentLength() {
            return maxContentLength;
        }

        public Settings setMaxContentLength(int maxContentLength) {
            this.maxContentLength = maxContentLength;
            return this;
        }

        /**
         * @return The minimum length of any posted content
         */
        public int getMinContentLength() {
            return minContentLength;
        }

        public Settings setMinContentLength(int minContentLength) {
            this.minContentLength = minContentLength;
            return this;
        }

        /**
         * @return The minimum length of the posted title
         */
        public int getMinTitleLength() {
            return minTitleLength;
        }

        public Settings setMinTitleLength(int minTitleLength) {
            this.minTitleLength = minTitleLength;
            return this;
        }

        /**
         * @return The maximum length of the posted title
         */
        public int getMaxTitleLength() {
            return maxTitleLength;
        }

        public Settings setMaxTitleLength(int maxTitleLength) {
            this.maxTitleLength = maxTitleLength;
            return this;
        }

        /**
         * @return Whether to sanitize messages to stop cross site scripting attacks.
         */
        public boolean isSanitizeAllMessages() {
            return sanitizeAllMessages;
        }

        public Settings setSanitizeAllMessages(boolean sanitizeAllMessages) {
            this.sanitizeAllMessages = sanitizeAllMessages;
            return this;
        }
    }
}
