package Flimma.Models;

public class UserRating implements Comparable<UserRating> {

    private User user;
    private Film film;
    private double rating;

    public UserRating(User user, Film film, double rating) {
        this.user = user;
        this.film = film;
        this.rating = rating;
    }

    // getter methods
    public Film getFilm() {
        return film;
    }

    public User getUser() {
        return user;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int compareTo(UserRating o) {
        return Double.compare(o.rating, this.rating);
    }
}