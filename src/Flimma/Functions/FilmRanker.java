package Flimma.Functions;

import Flimma.Model.Database;
import Flimma.Model.Film;
import Flimma.Model.UserRating;

public final class FilmRanker {

    // min amount of votes for film needed to be recommended
    private final int MIN_VOTES_IMDB = 1000;
    private final int MIN_VOTES_FLIMMA = 2;

    private final double AVG_RATING_IMDB = getAvgRatingImdb();
    private final double AVG_RATING_FLIMMA = getAvgRatingFlimma();

    private final Database database;

    public FilmRanker(Database database) {
        this.database = database;
    }

    /**
     * Calculates a normalized score for a film based on all Flimma Ratings and Imdb Ratings.
     * @param film
     * @return Score between 0 and 1
     */
    public double getNormalizedRankScore(Film film) {
        return getRankScore(film) / 10.0;
    }

    /**
     * Calculates a score for a film based on all flimma ratings and imdb ratings.
     * @param film
     * @return Score of a film
     */
    public double getRankScore(Film film) {

        int imdbVotes = film.getImdbVotes();
        double imdbRating = film.getImdbRating();

        int flimmaVotes = film.getUserRatings().size();

        // flimma rating from 0-5
        double flimmaRating = (flimmaVotes > 0) ? film.getUserRatings().stream().mapToDouble(UserRating::getRating).sum() / flimmaVotes : 0;

        // flimma rating from 0-10 like imdb
        flimmaRating *= 2;


        double imdbRank = calculateWeight(imdbVotes, MIN_VOTES_IMDB, imdbRating, AVG_RATING_IMDB);
        double flimmaRank = calculateWeight(flimmaVotes, MIN_VOTES_FLIMMA, flimmaRating, AVG_RATING_FLIMMA);

        // only combine the imdb and flimma rank if flimmaVotes exist
        if (flimmaVotes > 0) {
            return (imdbRank + flimmaRank) / 2;
        }
        return imdbRank;
    }

    /**
     * Calculates the average rating of all imdb ratings
     * @return average rating
     */
    private double getAvgRatingImdb() {
        double imdbRatingSum = database.getFilms().stream().mapToDouble(Film::getImdbRating).sum();
        double imdbRatingCount = database.getFilms().size();

        return imdbRatingSum / imdbRatingCount;
    }

    /**
     * Calculates the average rating of all flimma ratings
     * @return average rating
     */
    private double getAvgRatingFlimma() {
        double flimmaRatingSum = database.getUserRatings().stream().mapToDouble(UserRating::getRating).sum();
        double flimmaRatingCount = database.getUserRatings().size();

        return flimmaRatingSum / flimmaRatingCount;
    }


    /**
     * Calculates the weight of a film like the imdb algorithm
     * @param votesCount
     * @param votesCountMin
     * @param rating
     * @param avgRating
     * @return weight
     */
    private double calculateWeight(int votesCount, int votesCountMin, double rating, double avgRating){
        // formula (imdB ranking): R = (v ÷ (v+m)) × R + (m ÷ (v+m)) × C
        // source: https://www.quora.com/What-algorithm-does-IMDB-use-for-ranking-the-movies-on-its-site

        return ((votesCount / (double)(votesCount + votesCountMin)) * rating +
                (votesCountMin / (double)(votesCount + votesCountMin)) * avgRating);
    }
}
