package com.example.assignment_1.presentation;

import com.example.assignment_1.FormServlet;
import com.example.assignment_1.logic.Message;
import com.example.assignment_1.logic.MessageBoard;
import com.example.assignment_1.logic.MessageCollection;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Presentation logic for a message board
 */
public class MessageBoardPresenter implements PagePresenter {

    private final static String MESSAGE_HEADER = "<p class=\"message_header\">%s<span class=\"username\">%s</span> <span class=\"time\"> . %s</span></p>";
    private final static String MESSAGE_TITLE = "<a href=\"%s\" class=\"message-title\">%s%s</a>";
    private final static String MESSAGE_REPLY = "<a href=\"%s\" class=\"message-reply\">%s%s</a>";

    private final MessageBoard board;

    public MessageBoardPresenter(MessageBoard board) {
        this.board = board;
    }

    @Override
    public void present(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        //add basic header - Note this can be improved by extracting this out and having some kind of HTML helper class
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        writer.println("<head>");
        writer.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        writer.println("    <title>Message Board</title>");
        writer.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"msgstyle.css\"/>");
        writer.println("    <script type=\"text/javascript\" src=\"validation.js\"></script>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("    <h1>Amazing Message Board</h1>");
        //insert our message board
        insertBoard(0,board,writer);
        writer.println("    <h2>Make new post</h2>");
        //append the form
        FormServlet.insertForm(writer);
        writer.println("</body>");
        writer.println("</html>");
    }

    private void insertBoard(int indent, MessageCollection collection, PrintWriter writer) throws IOException {
        //perform depth first search
        for(Message message : collection) {
            writer.write(String.format(MESSAGE_HEADER,getIndent(indent),message.getFormattedUserName(), message.getDatePosted("MMM dd,yyyy HH:mm")));
            //treat replies differently
            if(message.isReply()) {
                writer.write(String.format(MESSAGE_REPLY, getURI(message.getMessageId()), getIndent(indent), message.getFormattedTitle()));
            } else {
                writer.write(String.format(MESSAGE_TITLE, getURI(message.getMessageId()), getIndent(indent), message.getFormattedTitle()));
            }
            //add all the replies
            insertBoard(indent + 4,message,writer);
        }
    }

    private String getURI(int id) {
        return "message?id="+id;
    }

    private String getIndent(int spaces) {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<spaces;i++) {
            builder.append("&nbsp;");
        }
        return builder.toString();
    }
}