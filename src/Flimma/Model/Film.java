package Flimma.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Film {

    private final int id;

    private final String name;
    private final String plot;
    private final String relased;

    private final int imdbVotes;
    private final double imdbRating;

    private final Set<Director> directors;
    private final Set<Actor> actors;
    private final Set<String> genres;

    private final List<UserRating> userRatings;

    public Film(int id, String name, String plot, String genre, String released, int imdbVotes, double imdbRating) {
        this.id = id;
        this.name = name;
        this.plot = plot;
        this.genres = new HashSet<>();
        this.genres.add(genre);
        this.relased = released;

        this.directors = new HashSet<>();
        this.actors = new HashSet<>();

        this.imdbVotes = imdbVotes;
        this.imdbRating = imdbRating;

        this.userRatings = new ArrayList<>();
    }

    // getter methods
    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (name.endsWith(", The")) {
            return "The " + name.substring(0, name.length() - ", The".length());
        }
        return name;
    }

    public String getPlot() { return plot; }

    public String getRelased() { return relased; }

    public int getImdbVotes() {
        return imdbVotes;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public Set<Director> getDirectors() { return directors; }

    public Set<Actor> getActors() { return actors; }

    public Set<String> getGenres() { return genres; }

    public List<UserRating> getUserRatings() {
        return userRatings;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
