package com.example.assignment_1;

import com.example.assignment_1.logic.Message;
import com.example.assignment_1.logic.MessageBoard;
import com.example.assignment_1.logic.MessageCollection;
import com.example.assignment_1.logic.MessageCreator;
import com.example.assignment_1.util.WFormatter;

//class just used for testing
public class Main {

    public static void main(String[] args) {
        //whatever thing I happened to do last
        MessageBoard board = new MessageBoard();
        board.addReply(new MessageCreator("Gamer","Gamer"));
        System.out.println(board.getReply(1));
        Message reply =  board.addReply(new MessageCreator("Gamer","Gamer"));
        reply.addReply(new MessageCreator("Gamer","Gamer"));
        writeMessages("",board);
    }

    public static void writeMessages(String indent, MessageCollection collection) {
        for(Message message : collection) {
            System.out.println(indent + message);
            writeMessages(indent + "  ",message);
        }
    }
}
