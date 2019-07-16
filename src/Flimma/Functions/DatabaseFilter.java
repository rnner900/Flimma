package Flimma.Functions;

import Flimma.Model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class DatabaseFilter {

    private final Database database;

    public DatabaseFilter(@NotNull Database database) {
        this.database = database;
    }

    public List<Film> filterGenre(List<Film> films, String genre) {
        if (genre != null) {
            // filter by genre
            return films.stream().filter(f -> f.getGenres().contains(genre)).collect(Collectors.toList());
        }
        return films;
    }

    public List<Film> filterName(@NotNull List<Film> films, @NotNull String filmname) {
         // filter by filmname
        return films.stream().filter(f -> f.getName().toLowerCase().contains(filmname.toLowerCase())).collect(Collectors.toList());
    }

    public List<Film> filterActor(@NotNull List<Film> films, @NotNull String actor) {
        // filter by actor
        List<Actor> actors = database.getActors();
        Actor actorMatch = actors.stream()
                .filter(a -> a.getName().toLowerCase().contains(actor.toLowerCase()))
                .findFirst()
                .orElse(null);

        return films.stream().filter(f -> f.getActors().contains(actorMatch)).collect(Collectors.toList());
    }

    public List<Film> filterDirector(@NotNull List<Film> films, @NotNull String director) {
        // filter by director
        List<Director> directors = database.getDirectors();
        Director directorMatch = directors.stream()
                .filter(d -> d.getName().toLowerCase().contains(director.toLowerCase()))
                .findFirst()
                .orElse(null);

        return films.stream().filter(f -> f.getDirectors().contains(directorMatch)).collect(Collectors.toList());
    }

    public List<Film> limit(@NotNull List<Film> films, int limit) {
        if (limit > 0)
        {
            // limit results
            return films.stream().limit(limit).collect(Collectors.toList());
        }
        return films;
    }

    public List<Film> filterRatedBy(List<Film> films, String ratedBy) {
        if (ratedBy != null) {
            // get films rated by a user
            User ratedByUser = database.getUsers().stream().filter(u -> u.getUserName().equals(ratedBy)).findFirst().orElse(null);
            if (ratedByUser != null) {
                return films.stream().filter(f -> ratedByUser.getUserRating(f) != null).collect(Collectors.toList());
            } else {
                System.out.println("User not found!");
            }
        }
        return films;
    }
}
