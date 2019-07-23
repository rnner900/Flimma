package Flimma;

import Flimma.Functions.InputException;
import Flimma.Model.Database;
import Flimma.Model.User;
import Flimma.Page.LoginPage;
import Flimma.Page.Page;
import Flimma.Page.StartPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    private static User user;
    private static Database database;

    /**
     * runs the Applicatio loop, print pages, respond on user input (interactive-mode called by Main)
     * @param pDatabase database
     */
    static void run(Database pDatabase) { // package-private
        database = pDatabase;

        Page activePage = new StartPage(database);
        Input input = new Input("");

        while (activePage != null)
        {
            // check for login
            if (user == null) {
                activePage = new LoginPage(database);
            }

            // show page if last input was not 'help'
            if (!input.getCmd().equals("help")) {
                activePage.show();

            }

            // user input
            System.out.print("> ");
            input = waitForInput();

            if (input.getCmd().equals("help")) {
                // print help of current page
                activePage.printHelp();
            }
            else if (input.getCmd().equals("exit")) {
                // exit application
                activePage = null;
            }
            else {
                try {
                    // call current page onInput. It returns the next page to show
                    activePage = activePage.onInput(input);
                    clearPage();

                } catch (InputException e) {
                    // page will throw error if input is not valid (index out of range for missing arguments for example)
                    System.out.println("Please check your input. Type in 'help' to get more information...");
                }
            }
        }
    }

    /**
     * set the current user by a username. If the user does not exist a new user will be created and logged in
     * @param username
     * @return
     */
    public static boolean setUser(String username) {
        user = database.getUser(username);

        if (user == null) {
            user = new User(username);
            database.getUsers().add(user);
        }

        return true;
    }

    /**
     * @return currently loggen in user
     */
    public static User getUser() {
        return user;
    }

    /**
     * waits for a user input
     * @return user input
     */
    private static Input waitForInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();
            return new Input(input);

        } catch (IOException e) {
            e.printStackTrace();
            return new Input("");
        }
    }

    /**
     * clear the console
     */
    private static void clearPage() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
}
