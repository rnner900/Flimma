package Flimma.Functions;

import Flimma.Models.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Determines recommendation related to different scores in criteria
 * <pre>
 * ALGORITHM:
 *
 *     For recommendations there are 4 criteria (ratings, genre, actor, director).
 *     Based on 'my' personal ratings we can determine likes (scores) for each criteria:
 *
 *     Genre-Score (Genre : Integer):
 *         I like a film of a genre. The score for the genre will be incremented.
 *         Example: i liked three films of genre 'Animation'. Therefore the score of 'Animation' is 3
 *
 *     Actor-Score (Actor : Integer):
 *         I liked a film with actors. The score of each actor will be incremented.
 *         Example: i liked four films with actor 'a'. Therefore the score of 'a' is 4
 *
 *     Director-Score (Director : Integer):
 *         I liked a film with directors. The score of each director will be incremented.
 *         Example: i liked four films with director 'b'. Therefore the score of 'b' is 4
 *
 *     Similarity-Score (Film : Integer):
 *         Another user also liked one of 'my' liked film. The score for all film the other user also liked will be incremented.
 *         Example: i liked film 'a'. Two other users, who also liked film 'a', like film 'b'. Therefore the score of film 'b' is 2
 *
 *     Ranking-Score (Film : Integer):
 *         Global ranking score for film determined by Imdb and all Flimma ratings. Not related to 'my' personal ratings
 *         Example: film 'c' is on 5th global rank. Therefore its score is 0.95. A score of 1 is the first global rank.
 *
 *     Each score will be normalized (divided by the max score of criteria).
 *     Therefore the scores of each criteria lay between 0 and 1.
 *     Example: A Genre is the most liked of all genres. Therefore its score will be normalized to 1.
 *              A Actor 'a' has a score of 5. The best Actor 'b' has a score of 15. Therefore the score of 'a' will be 0.333
 *
 *
 * - Setup the Scores (Manually):
 *     for the static-mode we need add functions to increment the scores manually.
 *     We leave all scores at 0 except the entities we want to select
 *
 * - To get a Recommendation:
 *     1. Looking through all films of database
 *     2. lookup the scores of each criteria and take the sum of them as the total-score.
 *         At this point we can set different weights for each criteria
 *         Note:    if there are multiple actors with a score in a film, the film can score more 1 for the actor criteria (same for directors)
 *
 *     3. sort all films by their total-score.
 *     4. take first n films as a result.
 *
 * - Let's go!
 * </pre>
 */


public final class FilmRecommender {

    // If the my rating for a film is lower that this it will not be used for recommending similar films
    private final float RECOMMEND_CLIPPING = 3.5f;

    private final Database database;

    private final Map<Film, Integer> similarityScores;
    private final Map<String, Integer> genreScores;
    private final Map<Actor, Integer> actorScores;
    private final Map<Director, Integer> directorScores;

    private final Set<Film> ignoredFilms;

    private int limit;

    // max scores for normalizing
    private int similarityScoreMax;
    private int genreScoreMax;
    private int actorScoreMax;
    private int directorScoreMax;

    public FilmRecommender(final Database database) {
        this.database = database;

        similarityScores = new HashMap<>();
        genreScores = new HashMap<>();
        actorScores = new HashMap<>();
        directorScores = new HashMap<>();

        ignoredFilms = new HashSet<>();
    }


    /**
     * Add 1 to the score for all films similar users also liked
     * @param film a given film
     * @param ignoreUser user ignored from the usergroup if it is not null
     */
    public void addSimilarityScore(final Film film, User ignoreUser) {

        ignoredFilms.add(film);

        // increase rating score
        for (UserRating otherUserRating : film.getUserRatings()) {
            // user that also liked this film
            User otherUser = otherUserRating.getUser();

            // skip if it is 'my' rating
            if (otherUser == ignoreUser) {
                continue;
            }

            for (UserRating similarUserRating : otherUser.getUserRatings()) {
                // skip same ratings
                if (otherUserRating == similarUserRating) {
                    continue;
                }

                Film similarFilm = similarUserRating.getFilm();

                Integer similarityScore = similarityScores.getOrDefault(similarFilm, 0) + 1;
                similarityScores.put(similarFilm, similarityScore);

                if (similarityScore > similarityScoreMax) {
                    similarityScoreMax = similarityScore;
                }
            }
        }
    }

    /**
     * Add 1 to the score of a given genre
     * @param genre given genre
     */
    public void addGenreScore(final String genre) {
        Integer genreScore = genreScores.getOrDefault(genre, 0) + 1;
        genreScores.put(genre, genreScore);

        if (genreScore > genreScoreMax) {
            genreScoreMax = genreScore;
        }
    }

    /**
     * Add 1 to the score of a given actor
     * @param actor given actor
     */
    public void addActorScore(final Actor actor) {
        Integer actorScore = actorScores.getOrDefault(actor, 0) + 1;
        actorScores.put(actor, actorScore);

        if (actorScore > actorScoreMax) {
            actorScoreMax = actorScore;
        }
    }

    /**
     * Add 1 to the score of a given director
     * @param director given director
     */
    public void addDirectorScore(final Director director) {
        Integer directorScore = directorScores.getOrDefault(director, 0) + 1;
        directorScores.put(director, directorScore);

        if (directorScore > directorScoreMax) {
            directorScoreMax = directorScore;
        }
    }

    /**
     * Set a limit for the resulting recommendations
     * @param limit limit
     */
    public void setLimit(final int limit) {
        this.limit = limit;
    }

    /**
     * Determine all scores by a user automatically
     * @param myUser user
     * @param limit limit
     */
    public void setScoresAuto(final User myUser, int limit) {

        this.limit = limit;

        for (UserRating myUserRating : myUser.getUserRatings()) {

            // increase similarity score
            Film film = myUserRating.getFilm();
            double rating = myUserRating.getRating();

            // skip film if 'i' didn't like the film (rating < 3.5)
            if (rating > RECOMMEND_CLIPPING) {
                addSimilarityScore(film, myUser);
            }

            // increase genre score
            for (String genre : film.getGenres()) {
                addGenreScore(genre);
            }

            // increase actor score
            for (Actor actor : film.getActors()) {
                addActorScore(actor);
            }

            // increase director score
            for (Director director : film.getDirectors()) {
                addDirectorScore(director);
            }
        }
    }

    /**
     * Get recommendations based on all scores
     * @return list of recommendatitons
     */
    public List<Film> getRecommendation() {

        // Finding Recommendations
        FilmRanker ranker = new FilmRanker(database);

        Map<Film, Double> filmScores = new HashMap<>();
        for (Film film : database.getFilms()) {

            if (ignoredFilms.contains(film)) {
                // ignore film if user rated it
                continue;
            }

            double rankingScore = ranker.getNormalizedRankScore(film); // returns value between 0 and 1

            // add normalized score
            double similarityScore = 0;
            if (similarityScoreMax > 0) {
                similarityScore = similarityScores.getOrDefault(film, 0) / (double) similarityScoreMax;
            }

            // genre
            double genreScore = 0;
            if (genreScoreMax > 0) {
                for (String genre : film.getGenres()) {
                    // add normalized score
                    genreScore += genreScores.getOrDefault(genre, 0) / (double) genreScoreMax;
                }
            }

            // actors
            double actorScore = 0;
            if (actorScoreMax > 0) {
                for (Actor actor : film.getActors()) {
                    // add normalized score
                    actorScore += actorScores.getOrDefault(actor, 0) / (double) actorScoreMax;
                }
            }

            // directors
            double directorScore = 0;
            if (directorScoreMax > 0) {
                for (Director director : film.getDirectors()) {
                    // add normalized score
                    directorScore += directorScores.getOrDefault(director, 0) / (double) directorScoreMax;
                }
            }

            double totalScore = (rankingScore + similarityScore + genreScore + actorScore + directorScore) / 5;

            filmScores.put(film, totalScore);
        }

        Stream<Map.Entry<Film, Double>> sorted = filmScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        return sorted.map(Map.Entry::getKey).limit(limit).collect(Collectors.toList());
    }
}
