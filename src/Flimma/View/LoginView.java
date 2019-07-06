package Flimma.View;

import Flimma.Main;

public class LoginView extends View {
    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printColumnB("Flimma Log In");
        table.printLine();

        System.out.println("Please enter a username:");
    }

    @Override
    public View onInput(String input) {

        if (Main.logIn(input)) {
            return new StartView();
        }
        return null;
    }

    @Override
    public void printHelp() {
        System.out.println("Please enter a username:");
    }
}
