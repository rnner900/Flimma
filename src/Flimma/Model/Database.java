package Flimma.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Map<String, User> users;

    private List<Film> films;
    private List<Actor> actors;
    private List<Director> directors;

    private List<UserRating> userRatings;


    // save map for saving ratings on the go
    private Map<Film, Integer> filmIds;

    public Database() {
        users = new HashMap<>();
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

            // add rating additionaly to database
            userRatings.add(userRating);
        }
        else {
            // System.out.println("A Rating for the user and film already exists. The rating will be overwritten");
            userRating.setRating(rating);
        }
    }

    // getter methods
    public Map<String, User> getUsers() {
        return users;
    }

    public List<Film> getFilms() {
        return films;
    }

    public Map<Film, Integer> getFilmIds() { return filmIds; }

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
