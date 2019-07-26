package Flimma.Pages;

import Flimma.Application;
import Flimma.Functions.FilmRecommender;
import Flimma.Functions.InputException;
import Flimma.Input;
import Flimma.Models.Database;
import Flimma.Models.Film;
import Flimma.Table;

import java.util.List;

/**
 * Start page, start menu of the application
 */
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
    public Page onInput(String input) throws InputException {
        Page result;

        String cmd = input.split(" ")[0];

        switch (cmd) {
            case "list":
                // list database content
                result = new ListPage(database);
                break;
            case "logout":
                // logout
                result = new LoginPage(database);
                break;
            case "recommend":
                int limit = Input.inputInt("Enter a limit");
                FilmRecommender fr = new FilmRecommender(database);
                fr.setScoresAuto(Application.getUser(), limit);
                List<Film> films = fr.getRecommendation();

                ListPage listPage = new ListPage(database);
                listPage.setFilms(films);
                result = listPage;
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
        table.printRow("list", "list & filter & rate films");
        table.printRow("recommend", "get recommendation");
        table.printRow("logout", "logout");
        table.printRow("exit", "exit program");
        table.printLine();
    }
}
