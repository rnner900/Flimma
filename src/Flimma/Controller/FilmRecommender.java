package Flimma.Controller;

import Flimma.Model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmRecommender {

    // If the my rating for a film is lower that this it will not be used for recommending similar films
    private static final float RECOMMEND_CLIPPING = 3.5f;

    private Database database;
    private FilmRanker filmRanker;


    public FilmRecommender(Database database) {
        this.database = database;
    }

    public List<Match> getRecommendation(User myUser, int limit) {

        /* ALGORITHM:
        For recommendations there are 4 criteria (ratings, genre, actor, director).
        Based on our ratings we can determine scores for each criteria.

        ratings     a score for each film another user liked, who also liked my films.
        genre       a score for each genre i liked
        actor       a score for each actor i liked
        director    a score for each director i liked

        a score will be increased for each match (Examples)
        ratings:    Two other users, who also liked film 'a', like film 'b'. Therefore the score of film 'b' is 2
        genre:      I liked three films of genre 'Animation'. Therefore the score of 'Animation' is 3
        actor:      i liked four films with actor 'a'. Therefore the score of 'a' is 4

        To combine multiple scores each score will be normalized (divided by the max score of criteria).

        Get Recommendation:
        Looking through all films for the highest match in all criteria-scores
         */

        Map<Film, Integer> ratingScores = new HashMap<>();
        Map<String, Integer> genreScores = new HashMap<>();
        Map<Actor, Integer> actorScores = new HashMap<>();
        Map<Director, Integer> directorScores = new HashMap<>();

        int ratingScoreMax = 0;
        int genreScoreMax = 0;
        int actorScoreMax = 0;
        int directorScoreMax = 0;

        // films i rated (ignored for recommendation)
        Set<Film> ratedFilms = new HashSet<>();

        for (UserRating myUserRating : myUser.getUserRatings()) {

            Film film = myUserRating.getFilm();
            ratedFilms.add(film);

            double rating = myUserRating.getRating();

            // skip film if not liked (rating < 3.5)
            if (rating < RECOMMEND_CLIPPING) {
                continue;
            }

            // for all users that also liked this film...
            for (UserRating otherUserRating : film.getUserRatings()) {

                User otherUser = otherUserRating.getUser();

                // skip if it is my rating
                if (otherUser == myUser) {
                    continue;
                }

                for (UserRating similarUserRating : otherUser.getUserRatings()) {

                    // skip same ratings
                    if (otherUserRating == similarUserRating) {
                        continue;
                    }

                    Film similarFilm = similarUserRating.getFilm();

                    Integer ratingScore = ratingScores.getOrDefault(similarFilm, 0) + 1;
                    ratingScores.put(similarFilm, ratingScore);

                    if (ratingScore > ratingScoreMax) {
                        ratingScoreMax = ratingScore;
                    }

                }
            }

            // increase genre score
            String genre = film.getGenre();
            Integer genreScore = genreScores.getOrDefault(genre, 0) + 1;
            genreScores.put(genre, genreScore);
            if (genreScore > genreScoreMax) {
                genreScoreMax = genreScore;
            }

            // increase actor score
            for (Actor actor : film.getActors()) {
                Integer actorScore = actorScores.getOrDefault(actor, 0) + 1;
                actorScores.put(actor, actorScore);

                if (actorScore > actorScoreMax) {
                    actorScoreMax = actorScore;
                }
            }

            // increase director score
            for (Director director : film.getDirectors()) {
                Integer directorScore = directorScores.getOrDefault(director, 0) + 1;
                directorScores.put(director, directorScore);
                if (directorScore > directorScoreMax) {
                    directorScoreMax = directorScore;
                }
            }
        }


        // Finding Recommendations
        Map<Film, Match> filmScores = new HashMap<>();
        for (Film film : database.getFilms()) {

            if (ratedFilms.contains(film)) {
                // ignore film if user rated it
                continue;
            }

            Match match = new Match(film);

            // similar ratings
            if (ratingScores.containsKey(film)) {
                double score = ratingScores.get(film) / (double)ratingScoreMax;
                match.setRatingScore(score);
            }

            // genre
            if (genreScores.containsKey(film.getGenre())) {
                double score = genreScores.get(film.getGenre()) / (double)genreScoreMax;
                match.setGenreScore(score);
            }

            // actors
            for (Actor actor : film.getActors()) {
                double score = 0;
                if (actorScores.containsKey(actor)) {
                    score += actorScores.get(actor) / (double)actorScoreMax;
                }
                match.setActorScore(score);
            }

            // directors
            for (Director director : film.getDirectors()) {
                double score = 0;
                if (directorScores.containsKey(director)) {
                    score += directorScores.get(director) / (double)directorScoreMax;
                }
                match.setDirectorScore(score);
            }
            filmScores.put(film, match);
        }

        Stream<Map.Entry<Film, Match>> sorted = filmScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        return sorted.map(Map.Entry::getValue).limit(limit).collect(Collectors.toList());
    }
}
