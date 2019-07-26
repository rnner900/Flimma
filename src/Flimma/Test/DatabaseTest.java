package Flimma.Test;

import Flimma.Functions.FilmRecommender;
import Flimma.Models.Actor;
import Flimma.Models.Database;
import Flimma.Models.Film;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tests the database (Unit Tests)
 */
public class DatabaseTest {

    private Database database;
    private BufferedWriter writer;


    public DatabaseTest(Database database) {
        this.database = database;
    }

    public void run(File logFile) {

        try {
            writer = new BufferedWriter(new FileWriter(logFile));

            try {
                testA();
            } catch (Exception e) {
                writer.write("TestA failed");
            }
            writer.newLine();

            try {
                testB();
            } catch (Exception e) {
                writer.write("TestB failed");
            }
            writer.newLine();

            try {
                testC();
            } catch (Exception e) {
                writer.write("TestC failed");
            }

            writer.close();
            System.out.println("Test complete. Results are stored in " + logFile.getName());

        } catch (IOException e) {
            System.out.println("Could not open log file");
        }

    }

    private void testA() throws IOException {

        final String genre = "Thriller";
        final String filmName = "Matrix Revolutions";
        final int limit = 10;

        Film film = database.getFilm(filmName);
        assert film != null;

        FilmRecommender recommender = new FilmRecommender(database);
        recommender.addGenreScore(genre);
        recommender.addSimilarityScore(film, null);
        recommender.setLimit(limit);

        writer.write("TestA: ");
        writer.newLine();

        for (Film f : recommender.getRecommendation()) {
            writer.write(f.getDisplayName());
            writer.newLine();
        }
    }

    private void testB() throws IOException {

        final String genre = "Adventure";
        final String filmName = "Indiana Jones and the Temple of Doom";
        final int limit = 15;

        Film film = database.getFilm(filmName);
        assert film != null;

        FilmRecommender recommender = new FilmRecommender(database);
        recommender.addGenreScore(genre);
        recommender.addSimilarityScore(film, null);
        recommender.setLimit(limit);

        writer.write("TestB: ");
        writer.newLine();

        for (Film f : recommender.getRecommendation()) {
            writer.write(f.getDisplayName());
            writer.newLine();
        }
    }

    private void testC() throws IOException {

        final String genre = "Action";
        final String actorNameA = "Jason Statham";
        final String actorNameB = "Keanu Reeves";
        final int limit = 50;

        Actor actorA = database.getActor(actorNameA);
        Actor actorB = database.getActor(actorNameB);
        assert actorA != null && actorB != null;

        FilmRecommender recommender = new FilmRecommender(database);
        recommender.addGenreScore(genre);
        recommender.addActorScore(actorA);
        recommender.addActorScore(actorB);
        recommender.setLimit(limit);

        recommender.getRecommendation();

        writer.write("TestC: ");
        writer.newLine();

        for (Film f : recommender.getRecommendation()) {
            writer.write(f.getDisplayName());
            writer.newLine();
        }
    }
}
