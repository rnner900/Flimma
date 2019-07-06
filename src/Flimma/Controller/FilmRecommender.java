package Flimma.Controller;

import Flimma.Main;
import Flimma.Model.*;
import Flimma.Page.Table;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FilmRecommender {

    // If the my rating for a film is lower that this it will not be used for recommending similar films
    private static final float RECOMMEND_CLIPPING = 3.5f;

    public List<Film> getRecommendation(int limit) {

        /* ALGORITHM:
        For recommendations there are 4 criteria (ratings, genre, actor, director).
        Based on 'my' personal ratings we can determine scores for each criteria:

        Rating-Score (Film -> Integer):
            Another user also liked a film. The score for all film the other user also liked will be incremented.
            Example: two other users, who also liked film 'a', like film 'b'. Therefore the score of film 'b' is 2

        Genre-Score (Genre -> Integer):
            I like a film of a genre. The score for the genre will be incremented.
            Example: i liked three films of genre 'Animation'. Therefore the score of 'Animation' is 3

        Actor-Score (Actor -> Integer):
            I liked a film with actors. The score of each actor will be incremented.
            Example: i liked four films with actor 'a'. Therefore the score of 'a' is 4

        Director-Score (Director -> Integer):
            I liked a film with director. The score of each director will be incremented.
            Example: i liked four films with actor 'a'. Therefore the score of 'a' is 4

        Each score will be normalized (divided by the max score of criteria).
        Therefore if a Genre is the most liked of all it will have a score of 1.

        To get a Recommendation:
        1. Looking through all films of database
        2. lookup the scores of each criteria and take the sum of them as the Total-Score.
            At this point we can set different weights for each criteria
            Note:    if more than one liked actor or director is in a film, the film can score more 1 for the criteria.
                     TODO: Normalize the actor and director sore for a film for more control
        3. sort all films by their Total-Score.
        4. take first n films as a result.


         */

        Database database = Main.getDatabase();
        User myUser = Main.getUser();


        Map<Film, Integer> ratingScores = new HashMap<>();
        Map<String, Integer> genreScores = new HashMap<>();
        Map<Actor, Integer> actorScores = new HashMap<>();
        Map<Director, Integer> directorScores = new HashMap<>();

        int ratingScoreMax = 0;
        int genreScoreMax = 0;
        int actorScoreMax = 0;
        int directorScoreMax = 0;

        // 'my' rated films (ignored as recommendation)
        Set<Film> myRatedFilms = new HashSet<>();

        for (UserRating myUserRating : myUser.getUserRatings()) {

            Film film = myUserRating.getFilm();
            myRatedFilms.add(film);

            double rating = myUserRating.getRating();

            // skip film if 'i' didn't like it (rating < 3.5)
            if (rating < RECOMMEND_CLIPPING) {
                continue;
            }


            for (UserRating otherUserRating : film.getUserRatings()) {

                // user that also liked this film
                User otherUser = otherUserRating.getUser();

                // skip if it is 'my' rating
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

            if (myRatedFilms.contains(film)) {
                // ignore film if user rated it
                continue;
            }

            Match match = new Match(film);


            // similar ratings
            if (ratingScores.containsKey(film)) {
                // add normalized score
                match.setRatingScore(ratingScores.get(film) / (double)ratingScoreMax);
            }

            // genre
            if (genreScores.containsKey(film.getGenre())) {
                match.setGenreScore(genreScores.get(film.getGenre()) / (double)genreScoreMax);
            }

            // actors
            for (Actor actor : film.getActors()) {
                double score = 0;
                if (actorScores.containsKey(actor)) {
                    score += actorScores.get(actor) / (double)actorScoreMax;
                }
                // TODO: Noramlize actorScore
                match.setActorScore(score);
            }

            // directors
            for (Director director : film.getDirectors()) {
                double score = 0;
                if (directorScores.containsKey(director)) {
                    score += directorScores.get(director) / (double)directorScoreMax;
                }
                // TODO: Noramlize directorScore
                match.setDirectorScore(score);
            }
            match.calculateTotalScore();
            filmScores.put(film, match);
        }

        Stream<Map.Entry<Film, Match>> sorted = filmScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));



        // print table of films
        Table table = new Table("%-20.20s", "%-5.5s", "%-5.5s", "%-5.5s", "%-5.5s");

        // print table head
        table.printLine();
        table.printColumnB("Name", "Ratings", "Genre", "Acotors", "Directors");
        table.printLine();

        for (Map.Entry<Film, Match> entry : sorted.limit(limit).collect(Collectors.toList())) {

            // print table content

            Match match = entry.getValue();
            table.printColumn(entry.getKey().getDisplayName(), match.getRatingScore(), match.getGenreScore(), match.getActorScore(), match.getDirectorScore());

        }
        // print table end
        table.printLine();

        return sorted.map(Map.Entry::getKey).limit(limit).collect(Collectors.toList());
    }
}
