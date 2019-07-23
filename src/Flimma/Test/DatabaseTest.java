package Flimma.Test;

import Flimma.Functions.FilmRecommender;
import Flimma.Model.Database;
import Flimma.Model.Film;

import java.io.File;

public class DatabaseTest {

    private Database database;

    public DatabaseTest(Database database) {
        this.database = database;
    }

    public void run(File logFile) {

    }

    private void testRecommendation() {

        final String genre = "Thriller";
        final String filmName = "Matrix Revolutions";
        final int limit = 10;

        Film film = database.getFilm(filmName);
        assert film != null;

        FilmRecommender recommender = new FilmRecommender(database);
        recommender.addGenreScore(genre);
    }
}
