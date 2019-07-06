package Flimma.Controller;

import Flimma.Model.Database;
import Flimma.Model.Film;
import Flimma.Model.UserRating;

import java.util.*;

public class FilmRanker {

    // min amount of votes for film needed to be recommended
    private static final int MIN_VOTES_IMDB = 1000;
    private static final int MIN_VOTES_FLIMMA = 2;

    private double avgRatingImdb;
    private double avgRatingFlimma;

    private List<Film> sortedFilms;

    public FilmRanker(Database database) {

        double flimmaRatingSum = database.getUserRatings().stream().mapToDouble(UserRating::getRating).sum();
        double flimmaRatingCount = database.getUserRatings().size();

        double imdbRatingSum = database.getFilms().stream().mapToDouble(Film::getImdbRating).sum();
        double imdbRatingCount = database.getFilms().size();

        avgRatingImdb = imdbRatingSum / imdbRatingCount;
        avgRatingFlimma = flimmaRatingSum / flimmaRatingCount;

        sortedFilms = new ArrayList<>(database.getFilms());
        // sort by film weights (decreasing)
        sortedFilms.sort((a, b) -> Double.compare(getWeight(b), getWeight(a)));
    }


    public List<Film> getHigherstRanked(int amount) {

        List<Film> result = new ArrayList<>();

        int size = sortedFilms.size();
        int i = 0;

        while (i < amount && i < size) {
            Film film = sortedFilms.get(i);
            result.add(film);
            i++;
        }

        return result;
    }

    private double getWeight(Film film) {

        int imdbVotes = film.getImdbVotes();
        double imdbRating = film.getImdbRating();

        int flimmaVotes = film.getUserRatings().size();

        // flimma rating from 0-5
        double flimmaRating = (flimmaVotes > 0) ? film.getUserRatings().stream().mapToDouble(UserRating::getRating).sum() / flimmaVotes : 0;

        // flimma rating from 0-10 like imdb
        flimmaRating *= 2;


        double imdbRank = calculateWeight(imdbVotes, MIN_VOTES_IMDB, imdbRating, avgRatingImdb);
        double flimmaRank = calculateWeight(flimmaVotes, MIN_VOTES_FLIMMA, flimmaRating, avgRatingFlimma);

        // only combine the imdb and flimma rank if flimmaVotes exist
        if (flimmaVotes > 0) {
            return (imdbRank + flimmaRank) / 2;
        }
        return imdbRank;
    }

    private double calculateWeight(int votesCount, int votesCountMin, double rating, double avgRating){
        // formula: R = (v ÷ (v+m)) × R + (m ÷ (v+m)) × C
        return (votesCount / (double)(votesCount + votesCountMin)) * rating +
                (votesCountMin / (double)(votesCount + votesCountMin)) * avgRating;
    }
}
