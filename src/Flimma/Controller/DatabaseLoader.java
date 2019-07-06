package Flimma.Controller;

import Flimma.Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class DatabaseLoader {

    // id look up tables for deserialization
    private Map<Integer, Actor> actorIds;
    private Map<Integer, Film> filmIds;
    private Map<Integer, Director> directorIds;

    // database to deserialize to
    private Database database;

    // loads a database with content of a file
    public void loadFromFile(Database database, File file) throws IOException {

        if (!file.exists()) {
            System.out.println("File not found");
            return;
        }

        this.database = database;

        actorIds = new HashMap<>();
        filmIds = new HashMap<>();
        directorIds = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {

            String line = br.readLine();

            // actors
            line = br.readLine();
            while (isEntity(line)) {
                loadActor(line);
                line = br.readLine();
            }

            // films
            line = br.readLine();
            while (isEntity(line)) {
                loadFilm(line);
                line = br.readLine();
            }

            // directors
            line = br.readLine();
            while (isEntity(line)) {
                loadDirector(line);
                line = br.readLine();
            }

            // actor -> films
            line = br.readLine();
            while (isEntity(line)) {
                loadActorFilm(line);
                line = br.readLine();
            }

            // director -> films
            line = br.readLine();
            while (isEntity(line)) {
                loadDirectorFilm(line);
                line = br.readLine();
            }

            // username -> rating
            line = br.readLine();
            while (isEntity(line)) {
                loadRating(line);
                line = br.readLine();
            }
        }

        /*
        System.out.println("Loaded:");
        System.out.println(database.getFilms().size() + " Films");
        System.out.println(database.getUsers().size() + " Users");
        System.out.println(database.getActors().size() + " Actors");
        System.out.println(database.getDirectors().size() + " Directors");
        System.out.println(database.getUserRatings().size() + " Ratings");
         */
    }

    private boolean isEntity(String line) {
        return line != null && !line.startsWith("New_Entity:");
    }

    private void loadActor(String line) {
        String[] values = parseValues(line);
        int id = Integer.parseInt(values[0]);

        if (!actorIds.containsKey(id)) {
            Actor actor = new Actor(values[1].trim());
            actorIds.put(id, actor);

            // add actor to database
            database.getActors().add(actor);
        }
    }
    private void loadFilm(String line) {
        String[] values = parseValues(line);
        int id = Integer.parseInt(values[0]);

        if (!filmIds.containsKey(id)) {
            if (values.length < 5) {
                System.out.println(line);
                System.out.println(Arrays.toString(values));
            }

            int imdbVotes = Integer.parseInt(values[5]);
            double imdbRating = Double.parseDouble(values[6]);

            Film film = new Film(values[1].trim(),values[2],values[3], values[4], imdbVotes, imdbRating, id);
            filmIds.put(id, film);

            // add film to database
            database.getFilms().add(film);
        }
    }
    private void loadDirector(String line) {
        String[] values = parseValues(line);
        int id = Integer.parseInt(values[0]);

        if (!directorIds.containsKey(id)) {
            Director director = new Director(values[1].trim());
            directorIds.put(id, director);

            // add director to database
            database.getDirectors().add(director);
        }
    }
    private void loadActorFilm(String line) {
        String[] values = parseValues(line);
        int actorId = Integer.parseInt(values[0]);
        int filmId = Integer.parseInt(values[1]);

        if (actorIds.containsKey(actorId) && filmIds.containsKey(filmId)) {
            Film film = filmIds.get(filmId);
            Actor actor = actorIds.get(actorId);

            // add actor to film
            film.getActors().add(actor);
        }
    }
    private void loadDirectorFilm(String line) {
        String[] values = parseValues(line);
        int directorId = Integer.parseInt(values[0]);
        int filmId = Integer.parseInt(values[1]);

        if (directorIds.containsKey(directorId) && filmIds.containsKey(filmId)) {
            Film film = filmIds.get(filmId);
            Director director = directorIds.get(directorId);

            // add director to film
            film.getDirectors().add(director);
        }
    }
    private void loadRating(String line) {
        String[] values = parseValues(line);
        String userName = values[0];
        int filmId = Integer.parseInt(values[2]);

        User user = database.getUsers().get(userName);
        if (user == null) {
            user = new User(userName);

            // add user to database
            database.getUsers().put(userName, user);
        }

        if (filmIds.containsKey(filmId)) {
            Film film = filmIds.get(filmId);
            double rating = Double.parseDouble(values[1]);

            // add rating to database
            database.addRating(user, film, rating);
        }
    }

    private String[] parseValues(String line) {
        line = line.substring(1, line.length()-1);  // remove first and last "
        return line.split("\",\"", -1); // split line by "," (also keep empty parts)
    }
}
