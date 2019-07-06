package Flimma.Page;

import Flimma.Controller.DatabaseFilter;
import Flimma.Main;
import Flimma.Model.*;
import Flimma.Model.UserRating;
import Flimma.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListPage implements Page {

    private static final int MAX_COUNT = 200;

    private List<Film> films;

    /**
     * Prints a table of a given list of films to the console.
     * The user can filter, recommend or rate films.
     *
     * @param films     a list of films
     */
    public ListPage(List<Film> films) {
        if (films == null) {
            films = new ArrayList<>();
        }
        this.films = films;
    }

    @Override
    public void show() {

        // print table of films
        Table table = new Table("%-5.5s", "%-30.30s", "%-30.30s", "%-8.8s", "%-10.10s", "%-30.30s", "%-30.30s", "%-5.5s", "%-5.5s", "%-5.5s", "%-5.5s");

        // print table head
        table.printLine();
        table.printColumnB("Id", "Name", "Plot", "Genre", "Released", "Actors", "Directors", "Imdb Votes", "Imdb ★", "Avg ★", "My ★");
        table.printLine();

        // print table content
        int count = Math.min(films.size(), MAX_COUNT);
        for (int i = 0; i < count; i++) {

            Film film = films.get(i);

            UserRating myUserRating = Main.getUser().getUserRating(film);
            double myRating = (myUserRating == null) ? 0 : myUserRating.getRating();
            myRating *= 2;

            // rating from 0-5
            double avgRating = film.getUserRatings().stream().mapToDouble(UserRating::getRating).sum() / film.getUserRatings().size();

            // rating form 0-10 like imdb
            avgRating *= 2;
            table.printColumn(i, film.getDisplayName(), film.getPlot(), film.getGenre(), film.getRelased(), actorsToString(film.getActors()), directorsToString(film.getDirectors()), film.getImdbVotes(), film.getImdbRating(), avgRating, myRating);
        }

        // print table end
        table.printLine();
    }

    @Override
    public Page onInput(String input) throws Exception {

        Page nextPage = this;
        String[] args = Parser.parseArgs(input);

        switch (args[0]) {
            // ACTIONS
            case "start":
                nextPage = new StartPage();
                break;

            case "rate":
                // rate a film
                int filmId = Integer.parseInt(args[1]);
                double rating = Double.parseDouble(args[2]);
                Film film = films.get(filmId);

                // add rating to database
                Main.getDatabase().addRating(Main.getUser(), film, rating);
                break;

            // FILTER
            case "genre":
                films = DatabaseFilter.filterGenre(films, args[1]);
                break;

            case "film":
                films = DatabaseFilter.filterName(films, args[1]);
                break;

            case "actor":
                films = DatabaseFilter.filterActor(films, args[1]);
                break;

            case "director":
                films = DatabaseFilter.filterDirector(films, args[1]);
                break;

            case "limit":
                int limit = Integer.parseInt(args[1]);
                films = DatabaseFilter.limit(films, limit);
                break;

            case "ratedBy":
                films = DatabaseFilter.filterRatedBy(films, args[1]);
                break;

            default:
                throw new Exception();
        }

        // return next page
        return nextPage;
    }

    @Override
    public void printHelp() {
        // print help
        Table table = new Table("%-25.25s","%-20.20s");
        table.printLine("ACTIONS","");
        table.printColumn("start", "go back to start");
        table.printColumn("rate      <filmId> <stars>", "rate a film");
        table.printColumn("recommend <amount>", "get recommendation");
        table.printColumn("", "");
        table.printLine("FILTER", "");
        table.printColumn("genre     <genre>", "filter by genre");
        table.printColumn("film      <filmname>", "filter by filmname");
        table.printColumn("actor     <actor>", "filter by actor");
        table.printColumn("director  <director>", "filter by director");
        table.printColumn("limit     <limit>", "limit results");
        table.printColumn("ratedBy   <username>", "get film rated by");
        table.printLine();
    }

    /**
     * Converts a Set of actors to a string for printing
     *
     * @param actors    a Set of actors
     * @return          the result string
     */
    private String actorsToString(Set<Actor> actors) {
        StringBuilder sb = new StringBuilder();
        for (Actor actor : actors) {
            sb.append(actor.getName()).append(", ");
        }
        return sb.toString();
    }

    /**
     * Converts a Set of directors to a string for printing
     *
     * @param directors a Set of directors
     * @return          the result string
     */
    private String directorsToString(Set<Director> directors) {
        StringBuilder sb = new StringBuilder();
        for (Director director : directors) {
            sb.append(director.getName()).append(", ");
        }
        return sb.toString();
    }
}
