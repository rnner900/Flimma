package Flimma.Model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;

    private List<UserRating> userRatings;

    public User(String userName) {
        this.userName = userName;
        this.userRatings = new ArrayList<>();
    }

    // getter methods
    public String getUserName() {
        return userName;
    }

    public List<UserRating> getUserRatings() {
        return userRatings;
    }

    public UserRating getUserRating(Film film) {
        return userRatings.stream()
                .filter(r -> r.getFilm() == film)
                .findFirst()
                .orElse(null);
    }
}
