package Flimma.Page;

import Flimma.Application;
import Flimma.Functions.DatabaseFilter;
import Flimma.Functions.DatabaseLoader;
import Flimma.Functions.InputException;
import Flimma.Input;
import Flimma.Main;
import Flimma.Model.Database;
import Flimma.Model.Film;
import Flimma.Model.UserRating;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ListPage extends Page {

    private static final int MAX_COUNT = 20;

    private List<Film> films;
    private int startIndex;

    public ListPage(Database database) {
        super(database);
        films = database.getFilms();
    }

    @Override
    public void show() {

        // print table of films
        Table table = new Table("%-5.5s", "%-30.30s", "%-30.30s", "%-20.20s", "%-10.10s", "%-30.30s", "%-30.30s", "%-7.7s", "%-5.5s", "%-6.6s", "%-5.5s");

        // print table head
        table.printLine();
        table.printRow("Id", "Name", "Plot", "Genre", "Released", "Actors", "Directors", "Imdb Votes", "Imdb ★", "Avg ★", "My ★");
        table.printLine();

        // print table content
        int count = Math.min(films.size(), MAX_COUNT);
        for (int i = 0; i < count; i++) {

            Film film = films.get(i);

            UserRating myUserRating = database.getUserRating(Application.getUser(), film);
            double myRating = (myUserRating == null) ? 0 : myUserRating.getRating();
            myRating *= 2;

            // rating from 0-5
            double avgRating = film.getUserRatings().stream().mapToDouble(UserRating::getRating).sum() / film.getUserRatings().size();

            // rating form 0-10 like imdb
            avgRating *= 2;
            table.printRow(i, film, film.getPlot(), collectionToString(film.getGenres()), film.getRelased(), collectionToString(film.getActors()), collectionToString(film.getDirectors()), film.getImdbVotes(), film.getImdbRating(), avgRating, myRating);
        }

        if (count < films.size()) {
            table.printRow("...", "...", "...", "...", "...", "...", "...", "...", "...", "...", "...");
        }

        // print table end
        table.printLine();
    }

    @Override
    public Page onInput(Input input) throws InputException {

        Page nextPage = this;

        DatabaseFilter df = new DatabaseFilter(database);

        switch (input.getCmd()) {
            // ACTIONS
            case "start":
                nextPage = new StartPage(database);
                break;

            case "next":
                startIndex += MAX_COUNT;
                break;

            case "rate":
                // rate a film
                int filmId = input.argToInt(1);
                double rating = input.argToDouble(2);
                Film film = films.get(filmId);

                // add rating to database
                UserRating userRating = database.addRating(Application.getUser(), film, rating);
                DatabaseLoader loader = new DatabaseLoader();
                try {
                    loader.saveRating(userRating, new File(Main.SAVE_PATH));
                } catch (IOException e) {
                    System.out.println("Error: Failed to save rating to file");
                }
                break;

            // FILTER
            case "genre":
                films = df.filterGenre(films, input.argToString(1));
                break;

            case "film":
                films = df.filterName(films, input.argToString(1));
                break;

            case "actor":
                films = df.filterActor(films, input.argToString(1));
                break;

            case "director":
                films = df.filterDirector(films, input.argToString(1));
                break;

            case "limit":
                films = df.limit(films, input.argToInt(1));
                break;

            case "ratedBy":
                films = df.filterRatedBy(films, input.argToString(1));
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
        Table table = new Table("%-30.30s","%-25.25s");
        table.printLine("ACTIONS","");
        table.printRow("start", "go back to start");
        table.printRow("next / prev", "show next / prev page");
        table.printRow("rate      <filmId> <stars>", "rate a film");
        table.printRow("recommend <amount>", "get recommendation");
        table.printRow("", "");
        table.printLine("FILTER", "");
        table.printRow("genre     <genre>", "filter by genre");
        table.printRow("film      <filmname>", "filter by filmname");
        table.printRow("actor     <actor>", "filter by actor");
        table.printRow("director  <director>", "filter by director");
        table.printRow("limit     <limit>", "limit results");
        table.printRow("ratedBy   <username>", "get film rated by");
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
        return sb.toString();
    }
}
