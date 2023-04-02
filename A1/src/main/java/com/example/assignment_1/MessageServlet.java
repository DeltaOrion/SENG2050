package com.example.assignment_1;

import com.example.assignment_1.logic.Message;
import com.example.assignment_1.logic.MessageForum;
import com.example.assignment_1.presentation.MessagePresenter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Control logic for presenting 'page B'. This essentially displays a user's message.
 */
@WebServlet(name = "MessageServlet",value = "/message")
public class MessageServlet extends HttpServlet {

    public static String ID_PARAMETER = "id";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        //one could further improve string logic by encapsulating it as a class, then having methods
        //like is null, is int, getInt() etc etc to encapsulate some of this logic.
        String idStr =  request.getParameter(ID_PARAMETER);
        //get int from id.
        if(idStr==null) {
            sendNotFoundPage(response,request);
            return;
        }

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.sendError(400,"ID is not a number");
            return;
        }
        //check if message exists
        Message message = MessageForum.getInstance().getRootBoard().getReply(id);
        if(message==null) {
            //if it's not found tell user
            sendNotFoundPage(response,request);
            return;
        }

        //present the message
        MessagePresenter presenter = new MessagePresenter(message);
        presenter.present(response);
    }

    private void sendNotFoundPage(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("text/html");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Message_Not_Found.html");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            response.sendError(500);
        }
    }
}
