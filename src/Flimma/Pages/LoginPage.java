package Flimma.Pages;

import Flimma.Application;
import Flimma.Models.Database;
import Flimma.Table;

/**
 * Login page, asks the user for login
 */
public class LoginPage extends Page {
    public LoginPage(Database database) {
        super(database);
    }

    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printRow("Flimma Log In");
        table.printLine();

        System.out.println("Please enter a username:");
    }

    @Override
    public Page onInput(String input) {

        if (Application.setUser(input)) {
            return new StartPage(database);
        }
        return this;
    }

    @Override
    public void printHelp() {
        System.out.println("Please enter a username:");
    }
}
