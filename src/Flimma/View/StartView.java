package Flimma.View;

import Flimma.Main;
import Flimma.Parser;

public class StartView extends View {
    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printColumnB("Hey " + Main.getUser().getUserName() + ", welcome to Flimma!");
        table.printColumn("");
        printHelp();
    }

    @Override
    public View onInput(String input) throws Exception {
        View result = null;

        String[] args = Parser.parseArgs(input);
        switch (args[0]) {
            case "list":
                // list database content
                result = new ListView(Main.getDatabase().getFilms());
                break;
            case "logout":
                // logout
                result = new LoginView();
                break;
            case "exit":
                // exit
                Main.exit();
                break;
            default:
                // invalid input
                throw new Exception();
        }

        return result;
    }

    @Override
    public void printHelp() {
        Table table = new Table("%-20.20s", "%-27.27s");
        table.printLine("ACTIONS", "");
        table.printColumn("list", "list database");
        table.printColumn("logout", "logout");
        table.printColumn("exit", "exit program");
        table.printLine();
    }
}
