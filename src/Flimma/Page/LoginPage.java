package Flimma.Page;

import Flimma.Main;

public class LoginPage implements Page {
    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printColumnB("Flimma Log In");
        table.printLine();

        System.out.println("Please enter a username:");
    }

    @Override
    public Page onInput(String input) {

        if (Main.logIn(input)) {
            return new StartPage();
        }
        return null;
    }

    @Override
    public void printHelp() {
        System.out.println("Please enter a username:");
    }
}
