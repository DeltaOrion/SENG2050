package com.example.assignment_2;

import com.example.assignment_2.game.Player;
import com.example.assignment_2.game.SecreteNumberGame;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A control class that manages save games. It is responsible for saving and loading games from persistent storage. It does all
 * the saving into persistent storage at the end to stop game states from potentially being rolled back. All games will be stored
 * in the /WEB-INF/Saves directory. Each game is saved in this directory with the name of the save being the username of the player.
 * Each save is saved in one file.
 *
 * Games are saved with the {@link #FILE_EXTENSION} extension.
 *
 * Games are also stored inside a relevant users' session.
 */
public class GameManager {

    //this was the easiest way to save games. Is it good system? Absolutely not!
    private final ConcurrentMap<String,SecreteNumberGame> saveGames;
    private Path persistenceFolder;
    private final static String GAME_ATTRIBUTE = "GAME";
    private final static String FILE_EXTENSION = ".sng";

    public GameManager() {
        saveGames = new ConcurrentHashMap<>();
    }

    /**
     * Initialises the game manager
     *   - loads all save games into memory.
     *
     * Saves will be stored in /WEB-INF/Saves
     *
     * @param webInfDir The location of the WEB-INF directory. This is where all the saves are stored.
     * @throws IOException If an error occurs while loading the saves.
     */
    public void init(Path webInfDir) throws IOException {
        persistenceFolder = webInfDir.resolve("saves");
        loadPersistentGames();
    }

    /**
     * Saves the given game. This currently will not save it in
     * persistent storage.
     *
     * @param game the game to save
     */
    public void saveGame(SecreteNumberGame game) {
        putGame(game);
    }

    /**
     * puts a game into the game cache.
     *
     * @param game the game to put into the cache.
     */
    private void putGame(SecreteNumberGame game) {
        saveGames.put(game.getPlayer().getUsername(),game);
    }

    /**
     * Loads a game from persistent storage as referenced by a username.
     *
     * @param username The username to reference
     * @return The game from memory.
     */
    public SecreteNumberGame loadGame(String username) {
        return saveGames.get(username);
    }

    /**
     * Saves all the games directly into persistent storage.
     *
     * @throws IOException If an error occurs while saving the games.
     */
    private void saveGames() throws IOException {
        //loop through games
        for(SecreteNumberGame game : saveGames.values()) {
            persistentlySaveGame(game);
        }
    }

    /**
     * Persistently saves the game into secondary storage. The game will be stored into a file
     * using the {@link #persistenceFolder}. The game is stored using the {@link Serializable} library.
     *
     * Each save game will be named "${name}.svg" where ${name} is the name of the player.
     *
     * @param game The game to be saved.
     * @throws IOException If an error occurs while saving the game
     */
    public void persistentlySaveGame(SecreteNumberGame game) throws IOException {
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOut = null;
        try {
            //create the file
            Path path = persistenceFolder.resolve(game.getPlayer().getUsername()+FILE_EXTENSION);

            //check that the file and directory exists. if not create it
            File file = path.toFile();
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if(!file.exists())
                file.createNewFile();

            //output using the serialization library.
            fileOut = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(game);
            objectOut.close();
        } finally {
            //once everything is done close both ostreams
            if(fileOut!=null)
                fileOut.close();

            if(objectOut!=null) {
                objectOut.close();
            }
        }
    }

    /**
     * Loads all the games from persistent storage. This will find any saves in the persistence directory and finds any files with
     * the username.
     *
     * @throws IOException if something goes wrong when reading the files.
     */
    public void loadPersistentGames() throws IOException {
        List<Path> gameFiles;
        //make the persistence folder if it doesnt exist
        if(!persistenceFolder.toFile().exists()) {
            persistenceFolder.toFile().mkdirs();
        }

        //find all of the files that are a valid save game
        try(Stream<Path> stream = Files.list(persistenceFolder)) {
            gameFiles = stream.filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION)).collect(Collectors.toList());
        }

        //loop through the game and load each individually
        for(Path path : gameFiles) {
            InputStream stream = new FileInputStream(path.toFile());
            loadSavedGame(stream);
        }
    }

    /**
     * Loads a game persistently from memory.
     *
     * @param stream The input steam to load from.;
     * @throws IOException If something goes wrong while loading a save game
     */
    private void loadSavedGame(InputStream stream) throws IOException {
        //create an istream from the file name
        ObjectInputStream objectinputstream = null;
        try {
            objectinputstream = new ObjectInputStream(stream);
            //if its not a valid game something went wrong
            SecreteNumberGame game = (SecreteNumberGame) objectinputstream.readObject();
            //add the game to the cache.
            putGame(game);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(objectinputstream!=null)
                objectinputstream.close();
        }
    }


    public void close() throws IOException {
        saveGames();
    }

    /**
     * Checks if a savegame already exists for this player.
     *
     * @param username The username to check
     * @return true if the save exists and false otherwise
     */
    public boolean contains(String username) {
        if(username==null)
            return false;

        return saveGames.containsKey(username);
    }

    /**
     * Retrieve a player game from their session.
     *
     * @param session The session to retrieve from
     * @return The game if it exists and null if it does not.
     */
    public SecreteNumberGame getGameFromSession(HttpSession session) {
        Object game = session.getAttribute(GAME_ATTRIBUTE);
        if(session.getAttribute(GAME_ATTRIBUTE) instanceof SecreteNumberGame)
            return (SecreteNumberGame) game;

        return null;
    }

    /**
     * Sets the sessions active game to the specified game.
     *
     * @param session The session to set
     * @param game The new game for the session.
     */
    public void setSessionGame(HttpSession session, SecreteNumberGame game) {
        session.setAttribute(GAME_ATTRIBUTE,game);
    }

}
