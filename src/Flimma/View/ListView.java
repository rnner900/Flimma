package Flimma.View;

import Flimma.Controller.DatabaseFilterer;
import Flimma.Main;
import Flimma.Model.*;
import Flimma.Model.UserRating;
import Flimma.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListView extends View {

    private static final int MAX_COUNT = 200;

    private List<Film> films;

    /**
     * Prints a table of a given list of films to the console.
     * The user can filter, recommend or rate films.
     *
     * @param films     a list of films
     */
    public ListView(List<Film> films) {
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
    public View onInput(String input) throws Exception {

        String[] args = Parser.parseArgs(input);

        Database database = Main.getDatabase();
        User myUser = Main.getUser();

        // filter for filtering films
        DatabaseFilterer filter = new DatabaseFilterer(database, films);

        View result = null;
        List<Film> newFilms = null;

        // check cmd and go to according page
        String cmd = args[0];

        switch (cmd) {
            // ACTIONS
            case "start":
                result = new StartView();
                break;

            case "rate":
                // rate a film
                int filmId = Parser.parseInt(args[1]);
                Film film = films.get(filmId);
                double rating = Parser.parseDouble(args[2]);
                database.addRating(myUser, film, rating);

                result = this;
                break;

            case "recommend":
                // get recommendations
                int amount = Parser.parseInt(args[1]);
                result = new RListView(amount);
                break;

            // FILTER
            case "genre":
                filter.genre(args[1]);
                newFilms = filter.toList();
                break;

            case "film":
                filter.name(args[1]);
                newFilms = filter.toList();
                break;

            case "actor":
                filter.actor(args[1]);
                newFilms = filter.toList();
                break;

            case "director":
                filter.director(args[1]);
                newFilms = filter.toList();
                break;

            case "limit":
                int limit = Parser.parseInt(args[1]);
                filter.limit(limit);
                newFilms = filter.toList();
                break;

            case "ratedBy":
                filter.ratedBy(args[1]);
                newFilms = filter.toList();
                break;

            default:
                throw new Exception();
        }

        if (newFilms != null) {
            result = new ListView(newFilms);
        }

        return result;
    }

    @Override
    public void printHelp() {
        // print help
        Table table = new Table("%-25.25s","%-20.20s");
        table.printLine("ACTIONS","");
        table.printColumn("start", "go back to start");
        table.printColumn("back", "go to previous view");
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
