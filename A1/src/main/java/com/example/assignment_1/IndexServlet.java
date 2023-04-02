package com.example.assignment_1;

import com.example.assignment_1.logic.MessageForum;
import com.example.assignment_1.presentation.MessageBoardPresenter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Control logic for 'page A'. This will create the 'index.html' or the entry page.
 */
@WebServlet(name = "IndexServlet")
public class IndexServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        //display the root message board.
        MessageBoardPresenter presenter = new MessageBoardPresenter(
                MessageForum.getInstance().getRootBoard()
        );
        presenter.present(response);
    }
}