package Flimma.Model;

public class Match implements Comparable<Match> {
    private Film film;

    // scores between 0 and 1:
    // 0 = no similarity
    // 1 = highest similarity (complete match with
    private double ratingScore;
    private double genreScore;
    private double actorScore;
    private double directorScore;

    private double totalScore;

    public Match(Film film) {
        this.film = film;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public double getGenreScore() {
        return genreScore;
    }

    public void setGenreScore(double genreScore) {
        this.genreScore = genreScore;
    }

    public double getActorScore() {
        return actorScore;
    }

    public void setActorScore(double actorScore) {
        this.actorScore = actorScore;
    }

    public double getDirectorScore() {
        return directorScore;
    }

    public void setDirectorScore(double directorScore) {
        this.directorScore = directorScore;
    }

    public void calculateTotalScore() {
        totalScore = (ratingScore + genreScore + actorScore + directorScore) / 4;
    }

    @Override
    public int compareTo(Match o) {
        return Double.compare(totalScore, o.totalScore);
    }

    public Film getFilm() {
        return film;
    }
}