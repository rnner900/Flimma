package Flimma;

import Flimma.Functions.DatabaseLoader;
import Flimma.Functions.FilmRecommender;
import Flimma.Model.Actor;
import Flimma.Model.Database;
import Flimma.Model.Director;
import Flimma.Model.Film;
import Flimma.Page.ListPage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static final String SAVE_PATH = "movieproject_original.db";

    public static void main(String[] args) {

        try {
            File file = new File(SAVE_PATH);

            Database database = new Database();

            DatabaseLoader loader = new DatabaseLoader();
            loader.loadFromFile(database, file);

            if (args != null && args.length > 0) {
                // Static mode
                loadStatic(database, args);
            }
            else {
                // Interactive mode
                loadInteractive(database);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * load static-mode with given args
     * @param database database
     * @param args given args
     */
    private static void loadStatic(Database database, String[] args) {

        FilmRecommender recommender = new FilmRecommender(database);

        for(String str : args) {

            String[] parts = str.split("=");

            switch (parts[0]) {
                case "--genre":
                    recommender.addGenreScore(parts[1]);
                    break;

                case "--film":
                    Film film = database.getFilm(parts[1]);
                    assert film != null;
                    recommender.addSimilarityScore(film, null);
                    break;

                case "--actor":
                    Actor actor = database.getActor(parts[1]);
                    assert actor != null;
                    recommender.addActorScore(actor);

                case "--director":
                    Director director = database.getDirector(parts[1]);
                    assert director != null;
                    recommender.addDirectorScore(director);

                case "--limit":
                    int limit = Integer.parseInt(parts[1]);
                    recommender.setLimit(limit);
                    break;
            }
        }

        List<Film> films = recommender.getRecommendation();

        ListPage result = new ListPage(database);
        result.setFilms(films);
        result.show();
    }

    /**
     * Loads the interactive-mode
     * @param database database
     */
    private static void loadInteractive(Database database) {
        Application.run(database);
    }


}
