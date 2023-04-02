package com.example.assignment_1;

import com.example.assignment_1.logic.Message;
import com.example.assignment_1.logic.MessageCollection;
import com.example.assignment_1.logic.MessageCreator;
import com.example.assignment_1.logic.MessageForum;
import com.example.assignment_1.presentation.MessageBoardPresenter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "replyForm", value = "/replyForm")
public class FormServlet extends HttpServlet {

    private static final String NUM_PARAMETER = "msg_num";
    private static final String NUM_BOARD = "board";
    private static final String USERNAME_PARAMETER = "username";
    private static final String TITLE_PARAMETER = "title";
    private static final String CONTENT_PARAMETER = "content";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String toPost = request.getParameter(NUM_PARAMETER);
        //find the message collection we are replying to.
        if(toPost==null) {
            //if no board is found send an error.
            response.setContentType("application/json");
            response.sendError(400,"Unknown Message Number");
            return;
        }

        if(toPost.equals(NUM_BOARD)) {
            //check if we are replying to the message board
            reply(MessageForum.getInstance().getRootBoard(),request,response);
            return;
        }

        int messageNumber = 0;
        try {
            //otherwise find the message number we are replying to
            messageNumber = Integer.parseInt(toPost);
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.sendError(400,"Message Number is not a number '"+toPost+"'");
            return;
        }

        //if the message does not exist
        Message message = MessageForum.getInstance().getRootBoard().getReply(messageNumber);
        if(message==null) {
            response.setContentType("application/json");
            response.sendError(400,"Unknown Message Number '"+toPost+"'");
            return;
        }

        reply(message,request,response);
    }

    private void reply(MessageCollection message, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter(USERNAME_PARAMETER);
        String content = request.getParameter(CONTENT_PARAMETER);
        String title = request.getParameter(TITLE_PARAMETER);

        try {
            //make a new message creator and attempt to add a reply
            message.addReply(new MessageCreator(title,content).setUserName(username).setPostTime(System.currentTimeMillis()));
            displayMainPage(request,response);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            //if it fails send the appropriate error message.
            response.setContentType("application/json");
            response.sendError(400,e.getMessage());
        }
    }

    private void displayMainPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        MessageBoardPresenter presenter = new MessageBoardPresenter(
                MessageForum.getInstance().getRootBoard()
        );
        presenter.present(response);
    }


    /**
     * Presents the form that would be used to generate a valid request for this servlet. Note, this will not work
     * unless the HTML page links the "validation.js" script!  This will attach the form to add a message to the
     * message board.
     *
     * @param writer The writer associated with the response to attach the form to
     *
     * @throws IOException
     */
    public static void insertForm(PrintWriter writer) throws IOException {
        insertForm(writer,NUM_BOARD);
    }

    /**
     * Presents the form that would be used to generate a valid request for this servlet. Note, this will not work
     * unless the HTML page links the "validation.js" script! This will attach the form to the message number
     * supplied.
     *
     * @param writer The writer associated with the response to attach the form to
     * @param message The message number this should reply to.
     * @throws IOException
     */
    public static void insertForm(PrintWriter writer, int message) throws IOException {
        insertForm(writer,String.valueOf(message));
    }

    /**
     * Presents the form that would be used to generate a valid request for this servlet. Note, this will not work
     * unless the HTML page links the "validation.js" script!
     *
     * @param writer The writer associated with the response to attach the form to
     * @param num The message num to reply to.
     * @throws IOException
     */
    private static void insertForm(PrintWriter writer, String num) throws IOException {
        //it probably would be better to dynamically construct the form with a method like addLine("username","etc",...);
        //if this is expanded upon in following assignments i will do.
        writer.println("    <form action=\"replyForm\" name=\"reply-form\" method=\"get\" onsubmit=\"return handleSubmit()\">");
        writer.println("        <label for=\""+ USERNAME_PARAMETER +"\">Username </label><label id=\"username_err\" class=\"err\"></label><br/>");
        writer.println("        <input type=\"text\" name=\""+ USERNAME_PARAMETER +"\" id=\"username\" /><br/>");
        writer.println("        <label for=\"" + TITLE_PARAMETER +"\">Title * </label><label id=\"title_err\" class=\"err\"></label><br/>");
        writer.println("        <input type=\"text\" name=\"" + TITLE_PARAMETER + "\" id=\"title\" /><br/>");
        writer.println("        <label for=\"" + CONTENT_PARAMETER + "\">Content * </label><label id=\"content_err\" class=\"err\"></label><br/>");
        writer.println("        <input type=\"text\" name=\"" + CONTENT_PARAMETER + "\" id=\"content\" /><br/>");
        writer.println("        <input type=\"hidden\" id=\"msg_num\" name=\"" + FormServlet.NUM_PARAMETER + "\" value=\"" + num +"\" />");
        writer.println("        <input type=\"submit\" /><br/>");
        writer.println("    </form>");
    }

}
