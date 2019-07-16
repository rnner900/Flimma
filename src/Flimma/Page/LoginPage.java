package Flimma.Page;

import Flimma.Application;
import Flimma.Input;
import Flimma.Model.Database;

public class LoginPage extends Page {
    public LoginPage(Database database) {
        super(database);
    }

    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printRowB("Flimma Log In");
        table.printLine();

        System.out.println("Please enter a username:");
    }

    @Override
    public Page onInput(Input input) {

        if (Application.setUser(input.getInput())) {
            return new StartPage(database);
        }
        return this;
    }

    @Override
    public void printHelp() {
        System.out.println("Please enter a username:");
    }
}
