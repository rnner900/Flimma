package Flimma;

import Flimma.Functions.InputException;
import Flimma.Models.Database;
import Flimma.Models.User;
import Flimma.Pages.LoginPage;
import Flimma.Pages.Page;
import Flimma.Pages.StartPage;

/**
 * Runtime application for interactive-mode
 */
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

        while (activePage != null)
        {
            // check for login
            if (user == null) {
                activePage = new LoginPage(database);
            }

            // show page
            activePage.show();

            boolean repeat = true;

            while(repeat) {

                repeat = false;

                // user input
                System.out.println();
                String pageName = activePage.getClass().getSimpleName().replace("Page", "");
                System.out.print("(" +  pageName + "): ");
                String input = Input.inputStr().trim();

                if (input.startsWith("help")) {
                    // print help of current page
                    activePage.printHelp();
                    repeat = true;

                } else if (input.startsWith("exit")) {
                    // exit application
                    activePage = null;

                } else {
                    try {
                        // call current page onInput. It returns the next page to show
                        activePage = activePage.onInput(input);
                        clearPage();

                    } catch (InputException e) {
                        // page will throw error if input is not valid (index out of range for missing arguments for example)
                        System.out.println("Please check your input. Type in 'help' to get more information...");
                        repeat = true;
                    }
                }
            }
        }
    }

    /**
     * set the current user by a username. If the user does not exist a new user will be created and logged in
     * @param username username
     * @return true if login was successful
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
     * clear the console
     */
    private static void clearPage() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
}
