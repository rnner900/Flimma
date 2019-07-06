package Flimma;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Flimma.Model.*;
import Flimma.Controller.*;
import Flimma.Page.*;

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

    // getter methods
    public static Database getDatabase() {
        return database;
    }

    public static User getUser() {
        return user;
    }

    // set user
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



    // load static mode
    private static void loadStatic(String[] args) {

        FilmRecommender recommender = new FilmRecommender();

        for(String str : args) {
            if (str.startsWith("--genre")) {
               // str.split("=")[1];
            }
            else if (str.startsWith("--film")) {
            }
            else if (str.startsWith("--limit")) {
                int limit =  Integer.parseInt(str.split("=")[1]);
            }
            else if (str.startsWith("--actor")) {
            }
            else if (str.startsWith("--director")) {
            }
            else if (str.startsWith("--ratedBy")) {
            }
        }

        List<Film> films = new ArrayList<>();

        ListPage result = new ListPage(films);

        result.show();
    }

    // load interactive mode
    private static void loadInteractive() {

        Page activePage = new StartPage();

        while (!exit)
        {
            // check for login
            if (getUser() == null) {
                activePage = new LoginPage();
            }

            // show page
            activePage.show();

            // user input
            System.out.print("> ");
            String input = waitForInput();

            if (input.startsWith("help")) {
                // print help of current page
                activePage.printHelp();
            }
            else {
                try {
                    // call current page onInput. It returns the next page to show
                    activePage = activePage.onInput(input);
                    clearPage();

                } catch (Exception e) {
                    System.out.println(e);
                    // page will throw error if input is not valid (index out of range for missing arguments for example)
                    System.out.println("Please check your input. Type in 'help' to get more information...");
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

    private static String waitForInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

    private static void clearPage() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
}
