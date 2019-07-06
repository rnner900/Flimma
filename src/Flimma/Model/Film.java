package Flimma.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Film {
    private String name;
    private String plot;
    private String genre;
    private String relased;

    private int imdbVotes;
    private double imdbRating;

    private Set<Director> directors;
    private Set<Actor> actors;

    private List<UserRating> userRatings;

    public Film(String name, String plot, String genre, String released, int imdbVotes, double imdbRating, int id) {
        this.name = name;
        this.plot = plot;
        this.genre = genre;
        this.relased = released;

        this.directors = new HashSet<>();
        this.actors = new HashSet<>();

        this.imdbVotes = imdbVotes;
        this.imdbRating = imdbRating;

        this.userRatings = new ArrayList<>();
    }

    // getter methods
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

    public String getGenre() { return genre; }

    public String getRelased() { return relased; }

    public int getImdbVotes() {
        return imdbVotes;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public Set<Director> getDirectors() { return directors; }

    public Set<Actor> getActors() { return actors; }

    public List<UserRating> getUserRatings() {
        return userRatings;
    }


}
