package Flimma.Page;

import Flimma.Application;
import Flimma.Functions.FilmRecommender;
import Flimma.Functions.InputException;
import Flimma.Input;
import Flimma.Model.Database;
import Flimma.Model.Film;

import java.util.List;

public class StartPage extends Page {

    public StartPage(Database database) {
        super(database);
    }

    @Override
    public void show() {
        Table table = new Table("%-50.50s");
        table.printLine();
        table.printRow("Hey " + Application.getUser().getUserName() + ", welcome to Flimma!");
        table.printLine();
        table.printRow("You can always type in 'help' to get help for");
        table.printRow("the page you are on and 'exit' to exit the app");
        table.printRow("");
        printHelp();
    }

    @Override
    public Page onInput(Input input) throws InputException {
        Page result = null;

        switch (input.getCmd()) {
            case "list":
                // list database content
                result = new ListPage(database);
                break;
            case "logout":
                // logout
                result = new LoginPage(database);
                break;
            case "recommend":
                int limit = input.argToInt(1);
                FilmRecommender fr = new FilmRecommender(database);
                fr.setScoresAuto(Application.getUser(), limit);
                List<Film> films = fr.getRecommendation();

                ListPage listPage = new ListPage(database);
                listPage.setFilms(films);
                break;
            default:
                // invalid input
                throw new InputException();
        }

        return result;
    }

    @Override
    public void printHelp() {
        Table table = new Table("%-20.20s", "%-27.27s");
        table.printLine("ACTIONS", "");
        table.printRow("list", "list database");
        table.printRow("logout", "logout");
        table.printRow("exit", "exit program");
        table.printLine();
    }
}
