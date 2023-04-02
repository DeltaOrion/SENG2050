package com.example.assignment_1.presentation;

import com.example.assignment_1.FormServlet;
import com.example.assignment_1.logic.Message;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MessagePresenter implements PagePresenter {

    private final Message message;

    public MessagePresenter(Message message) {
        this.message = message;
    }

    private final static String MESSAGE_HEADER = "<p class=\"message_header\"><span class=\"username\">%s</span> <span class=\"time\"> . %s</span></p>";
    private final static String MESSAGE_TITLE = "<h2>%s</h2>";
    private final static String MESSAGE_CONTENT = "<p class=\"content\">%s</p>";

    @Override
    public void present(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        //add basic header - Note this can be improved by extracting this out and having some kind of HTML helper class
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        writer.println("<head>");
        writer.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        writer.println("    <title>Message</title>");
        writer.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"msgstyle.css\"/>");
        writer.println("    <script type=\"text/javascript\" src=\"validation.js\"></script>");
        writer.println("</head>");
        writer.println("<body>");
        //append all message content
        writer.println(String.format(MESSAGE_TITLE,message.getFormattedTitle()));
        writer.println(String.format(MESSAGE_HEADER, message.getFormattedUserName(),message.getDatePosted("MMM dd,yyyy HH:mm")));
        writer.println(String.format(MESSAGE_CONTENT,message.getFormattedContent()));
        //append form only if they can reply
        if(!message.canAddReply()) {
            writer.println("<h2>Cannot add any more replies to this message :(</h2>");
        } else {
            writer.println("<h2>Reply to this Message</h2>");
            FormServlet.insertForm(writer, message.getMessageId());
        }
        writer.println("</body>");
        writer.println("</html>");
    }
}
