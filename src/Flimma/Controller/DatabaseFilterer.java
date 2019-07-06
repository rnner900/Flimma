package Flimma.Controller;

import Flimma.Model.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseFilterer {

    private Stream<Film> films;
    private Database database;

    public DatabaseFilterer(Database database, List<Film> films) {
        this.database = database;
        this.films = films.stream();
    }

    public void genre(String genre) {
        if (genre != null) {
            // filter by genre
            films = films.filter(f -> f.getGenre().equals(genre));
        }
    }

    public void name(String name) {
        if (name != null) {
            // filter by filmname
            films = films.filter(f -> f.getName().toLowerCase().contains(name.toLowerCase()));
        }
    }

    public void actor(String actor) {
        if (actor != null) {
            // filter by actor
            List<Actor> actors = database.getActors();
            Actor actorMatch = actors.stream()
                    .filter(a -> a.getName().toLowerCase().contains(actor.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            films = films.filter(f -> f.getActors().contains(actorMatch));
        }
    }

    public void director(String director) {
        if (director != null) {
            // filter by director
            List<Director> directors = database.getDirectors();
            Director directorMatch = directors.stream()
                    .filter(d -> d.getName().toLowerCase().contains(director.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            films = films.filter(f -> f.getDirectors().contains(directorMatch));
        }
    }

    public void limit(int limit) {
        if (limit > 0)
        {
            // limit results
            films = films.limit(limit);
        }
    }

    public void ratedBy(String ratedBy) {
        if (ratedBy != null) {
            // get films rated by a user
            User ratedByUser = database.getUsers().get(ratedBy);
            if (ratedByUser != null) {
                films = films.filter(f -> ratedByUser.getUserRating(f) != null);
            } else {
                System.out.println("User not found!");
            }
        }
    }

    public List<Film> toList() {
        return films.collect(Collectors.toList());
    }
}
