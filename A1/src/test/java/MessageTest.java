import com.example.assignment_1.logic.Message;
import com.example.assignment_1.logic.MessageBoard;
import com.example.assignment_1.logic.MessageCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {

    @Test
    public void boardTest() {
        MessageBoard board = new MessageBoard();
        Assertions.assertEquals(0,board.getReplies(true).size());
        Assertions.assertEquals(0,board.getReplies(false).size());

        for(int i=0;i<board.settings().getMaxReplies();i++) {
            Assertions.assertTrue(board.canAddReply());
            board.addReply(new MessageCreator("Gamer","Gamer"));
        }

        Assertions.assertEquals(board.settings().getMaxReplies(), board.getReplies(true).size());
        for(Message message : board.getReplies(true)) {
            message.addReply(new MessageCreator("Gamer","Gamer"));
        }

        for(Message message : board.getReplies(false)) {
            Assertions.assertTrue(message.getReplies(false).size()>0);
        }

    }

    @Test
    public void messageTest() {
        MessageBoard board = new MessageBoard();
        Message message = board.addReply(new MessageCreator("Gamer","Gamer")
                .setPostTime(System.currentTimeMillis())
                .setUserName("Gamer"));

        Message m = board.addReply(new MessageCreator("Gamer","Gamer"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {board.addReply(new MessageCreator("","Gamer"));});
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {board.addReply(new MessageCreator("Gamer",""));});

        StringBuilder builder = new StringBuilder("");
        for(int i=0;i<=board.settings().getMaxContentLength();i++) {
            builder.append("a");
        }

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {board.addReply(new MessageCreator("Gamer", builder.toString()));});

        Assertions.assertEquals(board.settings().getDefaultName(),m.getFormattedUserName());
        Assertions.assertEquals("Gamer",message.getFormattedUserName());

        message.addReply(new MessageCreator("Gamer","Gamer"))
                .addReply(new MessageCreator("Gamer","Gamer"))
                .addReply(new MessageCreator("Gamer","Gamer"));

        message.addReply(new MessageCreator("Gamer","Gamer"));

        Assertions.assertEquals(4,message.getReplies(true).size());

        for(int i=0;i<board.settings().getMaxReplies();i++) {
            m.addReply(new MessageCreator("Gamer","Gamer"));
        }

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {m.addReply(new MessageCreator("Gamer","Gamer"));});

    }
}
