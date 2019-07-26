package Flimma;

import Flimma.Functions.DatabaseLoader;
import Flimma.Functions.FilmRecommender;
import Flimma.Models.Actor;
import Flimma.Models.Database;
import Flimma.Models.Director;
import Flimma.Models.Film;
import Flimma.Pages.ListPage;
import Flimma.Test.DatabaseTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Main function of the program
 */
public class Main {

    public static final String SAVE_PATH = "movieproject.db";

    public static void main(String[] args) {

        File file = new File(SAVE_PATH);

        // load database
        Database database = new Database();

        DatabaseLoader loader = new DatabaseLoader();
        try {
            loader.loadFromFile(database, file);
        } catch (FileNotFoundException e) {
            System.out.println("File " + SAVE_PATH + " not found!");
            return;
        } catch (IOException e) {
            System.out.println("Something went wrong while loading the database");
            e.printStackTrace();
            return;
        }

        // load selected mode
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("--test=true")) {
            // test mode
            loadTest(database);
        }
        else if (argsList.size() > 0) {
            // static mode
            loadStatic(database, argsList);
        }
        else {
            // interactive mode
            loadInteractive(database);
        }

    }

    /**
     * Loads test-mode
     * @param database database
     */
    private static void loadTest(Database database) {
        DatabaseTest test = new DatabaseTest(database);
        test.run(new File("result.txt"));
    }

    /**
     * Loads static-mode with given args
     * @param database database
     * @param args given args
     */
    private static void loadStatic(Database database, List<String> args) {

        FilmRecommender recommender = new FilmRecommender(database);

        for(String str : args) {

            String[] parts = str.split("=");
            String cmd = parts[0];
            String[] values = parts[1].split(",");

            switch (cmd) {
                case "--genre":
                    for (String value : values) {

                        System.out.println("based on genre: " + value);
                        recommender.addGenreScore(value);
                    }
                    break;

                case "--film":
                    for (String value : values) {
                        Film film = database.getFilm(value);
                        assert film != null;

                        System.out.println("based on film: " + film.getDisplayName());
                        recommender.addSimilarityScore(film, null);
                    }
                    break;

                case "--actor":
                    for (String value : values) {
                        Actor actor = database.getActor(value);
                        assert actor != null;

                        System.out.println("based on actor: " + actor.getName());
                        recommender.addActorScore(actor);
                    }
                    break;

                case "--director":
                    for (String value : values) {
                        Director director = database.getDirector(value);
                        assert director != null;

                        System.out.println("based on director: " + director.getName());
                        recommender.addDirectorScore(director);
                    }
                    break;

                case "--limit":
                    for (String value : values) {
                        int limit = Integer.parseInt(value);
                        recommender.setLimit(limit);
                    }
                    break;
            }
        }

        List<Film> films = recommender.getRecommendation();

        ListPage result = new ListPage(database);
        result.setFilms(films);
        result.show();
    }

    /**
     * Loads interactive-mode
     * @param database database
     */
    private static void loadInteractive(Database database) {
        Application.run(database);
    }


}
