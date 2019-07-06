package Flimma.Page;

import Flimma.Controller.FilmRecommender;
import Flimma.Main;
import Flimma.Model.Film;
import Flimma.Parser;

import java.util.List;

public class StartPage implements Page {
    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printColumnB("Hey " + Main.getUser().getUserName() + ", welcome to Flimma!");
        table.printColumn("");
        printHelp();
    }

    @Override
    public Page onInput(String input) throws Exception {
        Page result = null;

        String[] args = Parser.parseArgs(input);
        switch (args[0]) {
            case "list":
                // list database content
                result = new ListPage(Main.getDatabase().getFilms());
                break;
            case "logout":
                // logout
                result = new LoginPage();
                break;
            case "exit":
                // exit
                Main.exit();
                break;
            case "recommend":
                int limit = Integer.parseInt(args[1]);
                List<Film> films = new FilmRecommender().getRecommendation(limit);

                result = new ListPage(films);
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
