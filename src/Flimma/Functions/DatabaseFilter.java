package Flimma.Functions;

import Flimma.Models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filters a list of films by criteria
 */
public final class DatabaseFilter {

    private final Database database;

    public DatabaseFilter(Database database) {
        this.database = database;
    }

    /**
     * Filters out all films that do not contain a given genre
     * @param films films to filter
     * @param genre given genre
     * @return filtered films
     */
    public List<Film> filterGenre(List<Film> films, String genre) {
        if (genre != null) {
            // filter by genre
            return films.stream().filter(f -> f.getGenres().contains(genre)).collect(Collectors.toList());
        }
        return films;
    }


    /**
     * Filters out all films that do not contain a given filmname
     * @param films films to filter
     * @param filmname given genre
     * @return filtered films
     */
    public List<Film> filterName(List<Film> films, String filmname) {
         // filter by filmname
        return films.stream().filter(f -> f.getName().toLowerCase().contains(filmname.toLowerCase())).collect(Collectors.toList());
    }


    /**
     * Filters out all films that do not contain a by name given actor
     * @param films films to filter
     * @param actor given actor name
     * @return filtered films
     */
    public List<Film> filterActor(List<Film> films, String actor) {
        // filter by actor
        List<Actor> actors = database.getActors();
        Actor actorMatch = actors.stream()
                .filter(a -> a.getName().toLowerCase().contains(actor.toLowerCase()))
                .findFirst()
                .orElse(null);

        return films.stream().filter(f -> f.getActors().contains(actorMatch)).collect(Collectors.toList());
    }


    /**
     * Filters out all films that do not contain a by name given actor
     * @param films films to filter
     * @param director given director name
     * @return filtered films
     */
    public List<Film> filterDirector(List<Film> films, String director) {
        // filter by director
        List<Director> directors = database.getDirectors();
        Director directorMatch = directors.stream()
                .filter(d -> d.getName().toLowerCase().contains(director.toLowerCase()))
                .findFirst()
                .orElse(null);

        return films.stream().filter(f -> f.getDirectors().contains(directorMatch)).collect(Collectors.toList());
    }


    /**
     * Limits the amount of films
     * @param films films to limit
     * @param limit limit
     * @return limited films
     */
    public List<Film> limit(List<Film> films, int limit) {
        if (limit > 0)
        {
            // limit results
            return films.stream().limit(limit).collect(Collectors.toList());
        }
        return films;
    }


    /**
     * Filters out all films that are not rated by a given user
     * @param films films to filter
     * @param userName username of given user
     * @return filtered films
     */
    public List<Film> filterRatedBy(List<Film> films, String userName) {
        if (userName != null) {

            User user = database.getUser(userName);
            if (user != null) {
                return films.stream().filter(f -> database.getUserRating(user, f) != null).collect(Collectors.toList());
            } else {
                System.out.println("User not found!");
            }
        }
        return films;
    }
}
