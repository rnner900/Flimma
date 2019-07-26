package Flimma.Functions;

import Flimma.Models.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads a database from file, saves UserRatings to file
 */
public final class DatabaseLoader {

    /**
     * loads a database with content of a file
     * @param database database to load in
     * @param file file to load from
     * @throws IOException exception
     */
    public void loadFromFile(Database database, File file) throws IOException {

        // id look up tables for deserialization
        Map<Integer, Actor> actors = new HashMap<>();
        Map<Integer, Film> films = new HashMap<>();
        Map<Integer, Director> directors = new HashMap<>();
        Map<String, User> users = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {

            String line = reader.readLine();

            // actors
            while (isEntity(line = reader.readLine())) {

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
            while (isEntity(line = reader.readLine())) {

                String[] values = parseValues(line);
                int filmId = parseInt(values[0], 0);

                if (!films.containsKey(filmId)) {
                    if (values.length < 5) {
                        System.out.println(line);
                        System.out.println(Arrays.toString(values));
                    }

                    int imdbVotes = parseInt(values[5], 0);
                    double imdbRating = parseDouble(values[6], 0.0);

                    Film film = new Film(filmId, values[1].trim(),values[2],values[3], values[4], imdbVotes, imdbRating);
                    films.put(filmId, film);

                    // add film to database
                    database.getFilms().add(film);
                }
                else {
                    Film film = films.get(filmId);
                    film.getGenres().add(values[3]);
                }
            }

            // directors
            while (isEntity(line = reader.readLine())) {

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
            while (isEntity(line = reader.readLine())) {

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
            while (isEntity(line = reader.readLine())) {

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
            while (isEntity(line = reader.readLine())) {

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
        // Show loading results
        System.out.println("Loaded:");
        System.out.println(database.getFilms().size() + " Films");
        System.out.println(database.getUsers().size() + " Users");
        System.out.println(database.getActors().size() + " Actors");
        System.out.println(database.getDirectors().size() + " Directors");
        System.out.println(database.getUserRatings().size() + " Ratings");
         */
    }

    /**
     * Serialized a rating to the end of the file <br>
     * If there already exists a rating in the file with the user and film it will be removed.
     * @param rating rating
     * @param file file to save to
     * @throws IOException exception
     */
    public void saveRating(UserRating rating, File file) throws IOException {

        File tempFile = new File("temp.db");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String username = rating.getUser().getUserName();
            int filmId = rating.getFilm().getId();

            String line;
            while ((line = reader.readLine()) != null) {
                if (!isUserRating(line, username, filmId)) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            writer.write("\"" + rating.getUser().getUserName() + "\",\"" + rating.getRating() + "\",\"" + filmId + "\"");
            writer.newLine();

        }

        if (file.delete()) {
            boolean success = tempFile.renameTo(file);
        }
    }

    /**
     * @param line line to check
     * @param username username of user
     * @param filmId id of film
     * @return true if the line is a rating of the user and film
     */
    private boolean isUserRating(String line, String username, int filmId) {
        return line.startsWith("\"" + username + "\"") &&
                line.endsWith("\"" + filmId + "\"");
    }

    /**
     * @param line line to check
     * @return true if the line is a parsable entry
     */
    private boolean isEntity(String line) {
        return line != null && !line.startsWith("New_Entity:");
    }


    /**
     * Helper method to parse string to integer
     * @param input string
     * @param defaultValue default value
     * @return the parsed input or default value
     */
    private static int parseInt(String input, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(input);
        }
        catch (Exception ignored) { }

        return defaultValue;
    }

    /**
     * Helper method to parse string to double
     * @param input string
     * @param defaultValue default value
     * @return the parsed input or default value
     */
    private static double parseDouble(String input, double defaultValue) {
        try {
            defaultValue = Double.parseDouble(input);
        }
        catch (Exception ignored) { }
        return defaultValue;
    }

    /**
     * Helper method to parse a line into values
     * @param line
     * @return
     */
    private String[] parseValues(String line) {
        line = line.substring(1, line.length()-1);  // remove first and last "
        return line.split("\",\"", -1); // split line by "," (also keep empty parts)
    }
}
