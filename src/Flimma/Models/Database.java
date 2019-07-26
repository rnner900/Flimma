package Flimma.Models;

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

    // add rating to database
    public UserRating addRating(User user, Film film, double rating) {
        UserRating userRating = getUserRating(user, film);

        if (userRating == null) {

            // add rating to user and film
            userRating = new UserRating(user, film, rating);

            user.getUserRatings().add(userRating);
            film.getUserRatings().add(userRating);

            // add rating additionally to database
            getUserRatings().add(userRating);
        }
        else {
            // System.out.println("A Rating for the user and film already exists. The rating will be overwritten");
            userRating.setRating(rating);
        }

        return userRating;
    }

    // special getter methods
    public User getUser(String userName) {
        return users.stream()
                .filter(u -> u.getUserName().equals(userName))
                .findFirst()
                .orElse(null);
    }

    public Actor getActor(String actorName) {
        return actors.stream()
                .filter(a -> a.getName().toLowerCase().contains(actorName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public Director getDirector(String directorName) {
        return directors.stream()
                .filter(d -> d.getName().toLowerCase().contains(directorName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public Film getFilm(String filmName) {
        return films.stream()
                .filter(f -> f.getName().toLowerCase().contains(filmName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public UserRating getUserRating(User user, Film film) {
        return user.getUserRatings().stream()
                .filter(r -> r.getFilm() == film)
                .findFirst()
                .orElse(null);
    }

    // getter methods
    public List<User> getUsers() {
        return users;
    }

    public List<Film> getFilms() {
        return films;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public List<UserRating> getUserRatings() {
        return userRatings;
    }
}
