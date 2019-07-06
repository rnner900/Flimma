package Flimma.Controller;

import Flimma.Model.*;
import Flimma.Model.UserRating;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseSaver {

    // saves a database to a file
    public void saveToFile(Database database, File file) throws IOException {

        // id look up tables for serialization
        Map<Actor, Integer> actorIds = new HashMap<>();
        Map<Film, Integer> filmIds = new HashMap<>();
        Map<Director, Integer> directorIds = new HashMap<>();


        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Actors
            writer.write("New_Entity: \"actor_id\",\"actor_name\"");
            writer.newLine();

            List<Actor> actors = database.getActors();
            for (int i = 0; i < actors.size(); i++) {
                Actor actor = actors.get(i);
                actorIds.put(actor, i);
                writer.write("\"" + i + "\",\"" + actor.getName() + "\"");
                writer.newLine();
            }

            // Films
            writer.write("New_Entity: \"movie_id\",\"movie_title\",\"movie_plot\",\"genre_name\",\"movie_released\",\"movie_imdbVotes\",\"movie_imdbRating\"");
            writer.newLine();

            List<Film> films = database.getFilms();
            for (int i = 0; i < films.size(); i++) {
                Film film = films.get(i);
                filmIds.put(film, i);
                writer.write("\"" + i + "\",\"" + film.getName() + "\",\"" + film.getPlot() + "\",\"" + film.getGenre() + "\",\"" + film.getRelased() + "\",\"" + film.getImdbVotes() + "\",\"" + film.getImdbRating());
                writer.newLine();
            }

            // Directors
            writer.write("New_Entity: \"director_id\",\"director_name\"");
            writer.newLine();

            List<Director> directors = database.getDirectors();
            for (int i = 0; i < directors.size(); i++) {
                Director director = directors.get(i);
                directorIds.put(director, i);
                writer.write("\"" + i + "\",\"" + director.getName() + "\"");
                writer.newLine();
            }


            // Actor -> Film
            writer.write("New_Entity: \"actor_id\",\"movie_id\"");
            writer.newLine();

            for (int i = 0; i < films.size(); i++) {
                Film film = films.get(i);
                for (Actor actor : film.getActors()) {
                    writer.write("\"" + actorIds.get(actor) + "\",\"" + i + "\"");
                    writer.newLine();
                }
            }

            // Director -> Film
            writer.write("New_Entity: \"director_id\",\"movie_id\"");
            writer.newLine();

            for (int i = 0; i < films.size(); i++) {
                Film film = films.get(i);
                for (Director director : film.getDirectors()) {
                    writer.write("\"" + directorIds.get(director) + "\",\"" + i + "\"");
                    writer.newLine();
                }
            }

            // Username -> Rating
            writer.write("New_Entity: \"user_name\",\"rating\",\"movie_id\"");
            writer.newLine();


            List<UserRating> ratings = database.getUserRatings();

            for (UserRating rating : ratings) {
                int filmId = filmIds.get(rating.getFilm());
                writer.write("\"" + rating.getUser().getUserName() + "\",\"" + rating.getRating() + "\",\"" + filmId + "\"");
                writer.newLine();
            }
        }
    }
}
