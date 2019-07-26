package Flimma.Functions;

import Flimma.Models.Database;
import Flimma.Models.Film;
import Flimma.Models.UserRating;

/**
 * Determines the global rank of a film
 *
 * <pre>
 * The formula used to determine a rank:
 * (imdB ranking): R = (v ÷ (v+m)) × R + (m ÷ (v+m)) × C
 *
 * source: https://www.quora.com/What-algorithm-does-IMDB-use-for-ranking-the-movies-on-its-site
 * </pre>
 */
public final class FilmRanker {

    // min amount of votes for film needed to be recommended
    private final int MIN_VOTES_IMDB = 1000;
    private final int MIN_VOTES_FLIMMA = 2;

    private final double AVG_RATING_IMDB;
    private final double AVG_RATING_FLIMMA;

    private final Database database;

    public FilmRanker(Database database) {
        this.database = database;

        AVG_RATING_IMDB = getAvgRatingImdb();
        AVG_RATING_FLIMMA = getAvgRatingFlimma();
    }

    /**
     * Calculates a normalized score for a film based on all Flimma Ratings and Imdb Ratings
     * @param film film
     * @return Score between 0 and 1
     */
    public double getNormalizedRankScore(Film film) {
        return getRankScore(film) / 10.0;
    }

    /**
     * Calculates a score for a film based on all flimma ratings and imdb ratings
     * @param film film
     * @return Score of a film
     */
    public double getRankScore(Film film) {

        int imdbVotes = film.getImdbVotes();
        double imdbRating = film.getImdbRating();

        int flimmaVotes = film.getUserRatings().size();

        // flimma rating from 0-5
        double flimmaRating = 0;
        if (flimmaVotes > 0) {
            flimmaRating = film.getUserRatings().stream()
                    .filter(r -> r.getRating() > 0)
                    .mapToDouble(UserRating::getRating)
                    .sum() / flimmaVotes;
        }

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
        double imdbRatingSum = database.getFilms().stream()
                .filter(f -> f.getImdbRating() > 0)
                .mapToDouble(Film::getImdbRating).sum();
        double imdbRatingCount = database.getFilms().size();

        return imdbRatingSum / imdbRatingCount;
    }

    /**
     * Calculates the average rating of all flimma ratings
     * @return average rating
     */
    private double getAvgRatingFlimma() {
        double flimmaRatingSum = database.getUserRatings().stream()
                .filter(r -> r.getRating() > 1)
                .mapToDouble(UserRating::getRating)
                .sum();
        double flimmaRatingCount = database.getUserRatings().size();

        return flimmaRatingSum / flimmaRatingCount;
    }

    /**
     * Calculates the weight of a film like the imdb algorithm
     * @param votesCount votes count
     * @param votesCountMin minimal votes count
     * @param rating rating
     * @param avgRating average rating
     * @return weight
     */
    private double calculateWeight(int votesCount, int votesCountMin, double rating, double avgRating){
        // formula (imdB ranking): R = (v ÷ (v+m)) × R + (m ÷ (v+m)) × C
        // source: https://www.quora.com/What-algorithm-does-IMDB-use-for-ranking-the-movies-on-its-site

        return ((votesCount / (double)(votesCount + votesCountMin)) * rating +
                (votesCountMin / (double)(votesCount + votesCountMin)) * avgRating);
    }
}
