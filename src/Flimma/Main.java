package Flimma;

import java.io.File;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import Flimma.Model.*;
import Flimma.Controller.*;
import Flimma.View.*;

public class Main {

    public static final String SAVE_PATH = "movieproject_original.db";

    private static Database database;
    private static User user;

    private static boolean exit;

    public static void main(String[] args) {

        try {
            File file = new File(SAVE_PATH);

            database = new Database();

            DatabaseLoader loader = new DatabaseLoader();
            loader.loadFromFile(database, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args != null && args.length > 0) {
            loadStatic(args);
        }
        else {
            loadInteractive();
        }
    }


    public static boolean logIn(String username) {
        user = database.getUsers().get(username);

        if (user == null) {
            user = new User(username);
            database.getUsers().put(username, user);
        }

        return true;
    }

    public static void exit() {
        // exit interactive mode
        exit = true;
    }

    // getter methods
    public static Database getDatabase() {
        return database;
    }

    public static User getUser() {
        return user;
    }

    // load interactive mode
    private static void loadInteractive() {

        View activeView = new LoginView();
        activeView.show();

        Stack<View> history = new Stack<>();

        while (!exit)
        {
            System.out.print("> ");
            String input = Input.waitForInput();

            if (input.startsWith("help")) {
                activeView.printHelp();
            }
            else if (input.startsWith("back")) {
                activeView = history.pop();
                clearPage();
                activeView.show();
            }
            else {
                View nextView = null;
                try {
                    nextView = activeView.onInput(input);
                } catch (Exception e) {
                    System.out.println("Invalid input. Type in 'help' to get more information...");
                }

                if (nextView != null) {
                    if (nextView != activeView) {
                        history.push(activeView);
                        activeView = nextView;
                    }
                    clearPage();
                    activeView.show();
                }
            }
        }

        // save before exit
        File file = new File(SAVE_PATH);
        DatabaseSaver saver = new DatabaseSaver();
        try {
            saver.saveToFile(database, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // load static mode
    private static void loadStatic(String[] args) {

        DatabaseFilterer filter = new DatabaseFilterer(database, database.getFilms());

        for(String str : args) {
            if (str.startsWith("--genre")) {
               filter.genre(str.split("=")[1]);
            }
            else if (str.startsWith("--film")) {
                filter.name(str.split("=")[1]);
            }
            else if (str.startsWith("--limit")) {
                int limit =  Integer.parseInt(str.split("=")[1]);
                filter.limit(limit);
            }
            else if (str.startsWith("--actor")) {
                filter.actor(str.split("=")[1]);
            }
            else if (str.startsWith("--director")) {
                filter.director(str.split("=")[1]);
            }
            else if (str.startsWith("--ratedBy")) {
                filter.ratedBy(str.split("=")[1]);
            }
        }

        List<Film> films = filter.toList();

        ListView result = new ListView(films);

        result.show();
    }

    private static void clearPage() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
}
