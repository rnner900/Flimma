package Flimma.Model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private final List<User> users;
    private final List<Film> films;
    private final List<Actor> actors;
    private final List<Director> directors;
    private final List<UserRating> userRatings;

    public Database() {
        users = new ArrayList<>();
        films = new ArrayList<>();
        actors = new ArrayList<>();
        directors = new ArrayList<>();
        userRatings = new ArrayList<>();
    }

    public void addRating(User user, Film film, double rating) {
        UserRating userRating = user.getUserRating(film);

        if (userRating == null) {

            // add rating to user and film
            userRating = new UserRating(user, film, rating);

            user.getUserRatings().add(userRating);
            film.getUserRatings().add(userRating);

            // add rating additionally to database
            userRatings.add(userRating);
        }
        else {
            // System.out.println("A Rating for the user and film already exists. The rating will be overwritten");
            userRating.setRating(rating);
        }
    }

    // getter methods
    @NotNull public List<User> getUsers() {
        return users;
    }

    @NotNull public List<Film> getFilms() {
        return films;
    }

    @NotNull public List<Actor> getActors() {
        return actors;
    }

    @NotNull public List<Director> getDirectors() {
        return directors;
    }

    @NotNull public List<UserRating> getUserRatings() {
        return userRatings;
    }
}
