package Flimma.Pages;

import Flimma.Application;
import Flimma.Functions.DatabaseFilter;
import Flimma.Functions.DatabaseLoader;
import Flimma.Functions.InputException;
import Flimma.Input;
import Flimma.Main;
import Flimma.Models.Database;
import Flimma.Models.Film;
import Flimma.Models.UserRating;
import Flimma.Table;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * List page, prints a list of films
 */
public class ListPage extends Page {

    private static final int MAX_COUNT = 200;

    private List<Film> films;
    private int startIndex;

    public ListPage(Database database) {
        super(database);
        films = database.getFilms();
    }

    @Override
    public void show() {

        // print table of films
        Table table = new Table("%-5.5s", "%-20.20s", "%-25.25s", "%-20.20s", "%-10.10s", "%-20.20s", "%-15.15s", "%-7.7s", "%-6.6s", "%-6.6s", "%-5.5s");

        // print table head
        table.printLine();
        table.printRow("Id", "Name", "Plot", "Genre", "Released", "Actors", "Directors", "Votes", "Imdb ★", "Avg ★", "My ★");
        table.printLine();

        // print table content
        int count = Math.min(films.size(), MAX_COUNT);
        for (int i = 0; i < count; i++) {

            Film film = films.get(i);


            double myRating = 0;
            if (Application.getUser() != null) {
                UserRating myUserRating = database.getUserRating(Application.getUser(), film);
                myRating = (myUserRating == null) ? 0 : myUserRating.getRating();
            }

            // rating from 0-5
            double avgRating = film.getUserRatings().stream()
                    .filter(r -> r.getRating() > 0)
                    .mapToDouble(UserRating::getRating)
                    .sum() / film.getUserRatings().size();

            table.printRow(i, film, film.getPlot(), collectionToString(film.getGenres()), film.getRelased(), collectionToString(film.getActors()), collectionToString(film.getDirectors()), film.getImdbVotes(), film.getImdbRating(), avgRating, myRating);
        }

        if (count < films.size()) {
            table.printRow("...", "...", "...", "...", "...", "...", "...", "...", "...", "...", "...");
        }

        // print table end
        table.printLine();
    }

    @Override
    public Page onInput(String input) throws InputException {

        Page nextPage;

        DatabaseFilter df = new DatabaseFilter(database);

        switch (input) {
            // ACTIONS
            case "start":
                nextPage = new StartPage(database);
                break;

            case "rate":
                // rate a film
                int filmId = Input.inputInt("Enter a film id");
                if (filmId < 0 || filmId >= films.size()) {
                    // id not in range
                    throw new InputException();
                }

                double rating = Input.inputDouble("Enter a rating from 1-5");

                if (rating < 1 || rating > 5) {
                    // rating not in range
                    throw new InputException();
                }

                Film film = films.get(filmId);

                // add rating to database
                UserRating userRating = database.addRating(Application.getUser(), film, rating);
                DatabaseLoader loader = new DatabaseLoader();
                try {
                    loader.saveRating(userRating, new File(Main.SAVE_PATH));
                } catch (IOException e) {
                    System.out.println("Error: Failed to save rating to file");
                }
                // show page again
                nextPage = this;
                break;

            // FILTER
            case "genre":
                String genre = Input.inputStr("Enter a genre");
                films = df.filterGenre(films, genre);
                nextPage = this;
                break;

            case "film":
                String filmName = Input.inputStr("Enter an film name");
                films = df.filterName(films, filmName);
                nextPage = this;
                break;

            case "actor":
                String actorName = Input.inputStr("Enter an actor name");
                films = df.filterActor(films, actorName);
                nextPage = this;
                break;

            case "director":
                String directorName = Input.inputStr("Enter a director name");
                films = df.filterDirector(films, directorName);
                nextPage = this;
                break;

            case "limit":
                int limit = Input.inputInt("Enter a limit");
                films = df.limit(films, limit);
                nextPage = this;
                break;

            case "ratedBy":
                String username = Input.inputStr("Enter a username");
                films = df.filterRatedBy(films, username);
                nextPage = this;
                break;

            default:
                throw new InputException();
        }

        // return next page
        return nextPage;
    }

    @Override
    public void printHelp() {
        // print help
        Table table = new Table("%-25.25s","%-25.25s");
        table.printLine("ACTIONS","");
        table.printRow("start", "go back to start");
        table.printRow("rate", "rate a film");
        table.printRow("", "");
        table.printLine("FILTER", "");
        table.printRow("genre", "filter by genre");
        table.printRow("film", "filter by filmname");
        table.printRow("actor", "filter by actor");
        table.printRow("director", "filter by director");
        table.printRow("limit", "limit results");
        table.printRow("ratedBy", "get film rated by");
        table.printLine();
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }

    private String collectionToString(Collection collection) {
        StringBuilder sb = new StringBuilder();

        for (Object object : collection) {
            sb.append(object).append(", ");
        }
        // remove last ',' from string
        return sb.toString();
    }
}
