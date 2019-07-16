package Flimma.Functions;

import Flimma.Model.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseLoader {

    // loads a database with content of a file
    public void loadFromFile(@NotNull Database database, @NotNull File file) throws IOException {

        if (!file.exists()) {
            System.out.println("File not found");
            return;
        }

        // id look up tables for deserialization
        Map<Integer, Actor> actors = new HashMap<>();
        Map<Integer, Film> films = new HashMap<>();
        Map<Integer, Director> directors = new HashMap<>();
        Map<String, User> users = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {

            String line = br.readLine();

            // actors
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                int id = parseInt(values[0], 0);

                if (!actors.containsKey(id)) {
                    Actor actor = new Actor(values[1].trim());
                    actors.put(id, actor);

                    // add actor to database
                    database.getActors().add(actor);
                }
            }

            // films
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                int id = parseInt(values[0], 0);

                if (!films.containsKey(id)) {
                    if (values.length < 5) {
                        System.out.println(line);
                        System.out.println(Arrays.toString(values));
                    }

                    int imdbVotes = parseInt(values[5], 0);
                    double imdbRating = parseDouble(values[6], 0.0);

                    Film film = new Film(values[1].trim(),values[2],values[3], values[4], imdbVotes, imdbRating);
                    films.put(id, film);

                    // add film to database
                    database.getFilms().add(film);
                }
                else {
                    Film film = films.get(id);
                    film.getGenres().add(values[3]);
                }
            }

            // directors
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                int id = parseInt(values[0], 0);

                if (!directors.containsKey(id)) {
                    Director director = new Director(values[1].trim());
                    directors.put(id, director);

                    // add director to database
                    database.getDirectors().add(director);
                }
            }

            // actor -> films
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                int actorId = parseInt(values[0], 0);
                int filmId = parseInt(values[1], 0);

                if (actors.containsKey(actorId) && films.containsKey(filmId)) {
                    Film film = films.get(filmId);
                    Actor actor = actors.get(actorId);

                    // add actor to film
                    film.getActors().add(actor);
                }
            }

            // director -> films
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                int directorId = parseInt(values[0], 0);
                int filmId = parseInt(values[1], 0);

                if (directors.containsKey(directorId) && films.containsKey(filmId)) {
                    Film film = films.get(filmId);
                    Director director = directors.get(directorId);

                    // add director to film
                    film.getDirectors().add(director);
                }
            }

            // username -> rating
            while (isEntity(line = br.readLine())) {

                String[] values = parseValues(line);
                String username = values[0];
                int filmId = parseInt(values[2], 0);

                User user = users.get(username);
                if (user == null) {
                    user = new User(username);
                    users.put(username, user);

                    // add user to database
                    database.getUsers().add(user);
                }

                if (films.containsKey(filmId)) {
                    Film film = films.get(filmId);
                    double rating = parseDouble(values[1], -1);

                    // add rating to database
                    database.addRating(user, film, rating);
                }
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

    private static int parseInt(String input, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(input);
        }
        catch (Exception ignored) { }

        return defaultValue;
    }

    private static double parseDouble(String input, double defaultValue) {
        try {
            defaultValue = Double.parseDouble(input);
        }
        catch (Exception ignored) { }
        return defaultValue;
    }

    private String[] parseValues(String line) {
        line = line.substring(1, line.length()-1);  // remove first and last "
        return line.split("\",\"", -1); // split line by "," (also keep empty parts)
    }
}
