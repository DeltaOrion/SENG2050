import com.example.assignment_2.game.GameState;
import com.example.assignment_2.game.Player;
import com.example.assignment_2.game.RoundEndOffer;
import com.example.assignment_2.game.SecreteNumberGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * H e l l o    M a r k e r
     *
     * This is a test class. Although this isn't even needed for the servlet I just left it in
     * to show that the MVC controller is so good and that I can completely swap out the control and view from a
     * web server to a console application!
     */
    public static void main(String[] args) {
        Player player = new Player("Jimmy");
        SecreteNumberGame game = new SecreteNumberGame(player);
        System.out.println("------ Hello "+player.getUsername()+"' -----------");
        System.out.println("Starting Game");
        game.start();
        System.out.println("CHEAT MODE - THE SECRETE NUMBER IS '"+game.getSecretNumber()+"'");
        while(!game.getState().equals(GameState.FINISH)) {
            System.out.println("ROUND "+game.getRound());
            System.out.println("Time to enter numbers");
            Scanner scanner = new Scanner(System.in);
            List<Integer> guesses = new ArrayList<>();
            for(int i=0;i<game.getGuesses();i++) {
                System.out.println("Enter Guess: ");
                guesses.add(Integer.parseInt(scanner.nextLine()));
            }

            game.submitGuesses(guesses);

            if(game.getState().equals(GameState.AWAITING_OFFER)) {
                System.out.println("The game is offering '"+game.getRoundEndPrize()+"' for you to quit");
                System.out.println("Revealed Numbers: "+game.getRevealedNumbers());
                System.out.println("Do you quit???");
                System.out.println("Current Balance '"+player.getMoney()+"'");
                System.out.println("Type 'true' to quit or anything else to not");
                boolean quit = Boolean.parseBoolean(scanner.nextLine());
                if(quit) {
                    game.submitRoundEndOffer(RoundEndOffer.ACCEPT);
                } else {
                    game.submitRoundEndOffer(RoundEndOffer.DENY);
                }
            }
        }
        if(game.getState().equals(GameState.FINISH)) {
            if(game.playerWon()) {
                System.out.println("You're a winner!");
            } else {
                System.out.println("You lost!");
                System.out.println("The secrete number was '"+game.getSecretNumber());
            }
        }
        System.out.println("Balance '"+player.getMoney()+"'");
    }
}
