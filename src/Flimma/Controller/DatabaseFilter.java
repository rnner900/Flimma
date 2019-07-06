package Flimma.Controller;

import Flimma.Main;
import Flimma.Model.*;

import java.util.List;
import java.util.stream.Collectors;

public final class DatabaseFilter {

    public static List<Film> filterGenre(List<Film> films, String genre) {
        if (genre != null) {
            // filter by genre
            return films.stream().filter(f -> f.getGenre().equals(genre)).collect(Collectors.toList());
        }
        return films;
    }

    public static List<Film> filterName(List<Film> films, String name) {
        if (name != null) {
            // filter by filmname
            return films.stream().filter(f -> f.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        }
        return films;
    }

    public static List<Film> filterActor(List<Film> films, String actor) {
        if (actor != null) {
            // filter by actor
            List<Actor> actors = Main.getDatabase().getActors();
            Actor actorMatch = actors.stream()
                    .filter(a -> a.getName().toLowerCase().contains(actor.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            return films.stream().filter(f -> f.getActors().contains(actorMatch)).collect(Collectors.toList());
        }
        return films;
    }

    public static List<Film> filterDirector(List<Film> films, String director) {
        if (director != null) {
            // filter by director
            List<Director> directors = Main.getDatabase().getDirectors();
            Director directorMatch = directors.stream()
                    .filter(d -> d.getName().toLowerCase().contains(director.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            return films.stream().filter(f -> f.getDirectors().contains(directorMatch)).collect(Collectors.toList());
        }
        return films;
    }

    public static List<Film> limit(List<Film> films, int limit) {
        if (limit > 0)
        {
            // limit results
            return films.stream().limit(limit).collect(Collectors.toList());
        }
        return films;
    }

    public static List<Film> filterRatedBy(List<Film> films, String ratedBy) {
        if (ratedBy != null) {
            // get films rated by a user
            User ratedByUser = Main.getDatabase().getUsers().get(ratedBy);
            if (ratedByUser != null) {
                return films.stream().filter(f -> ratedByUser.getUserRating(f) != null).collect(Collectors.toList());
            } else {
                System.out.println("User not found!");
            }
        }
        return films;
    }
}
