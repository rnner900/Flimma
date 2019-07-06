package Flimma.View;

import Flimma.Controller.FilmRecommender;
import Flimma.Main;
import Flimma.Model.Match;

import java.util.List;

public class RListView extends View {

    private int limit;
    public RListView(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void show() {

        FilmRecommender recommender = new FilmRecommender(Main.getDatabase());
        List<Match> matches = recommender.getRecommendation(Main.getUser(), limit);

        // print table of films
        Table table = new Table("%-5.5s", "%-30.30s", "%-30.30s", "%-8.8s", "%-10.10s", "%-30.30s", "%-30.30s");

        // print table head
        table.printLine();
        table.printColumnB("Id", "Name", "Match Ratings", "Match Genre", "Match Acotors", "Match Directors");
        table.printLine();

        // print table content
        int count = Math.min(matches.size(), limit);
        for (int i = 0; i < count; i++) {

            Match match = matches.get(i);
            table.printColumn(i, match.getFilm().getDisplayName(), match.getRatingScore(), match.getGenreScore(), match.getActorScore(), match.getDirectorScore());
        }

        // print table end
        table.printLine();
    }

    @Override
    public View onInput(String input) throws Exception {
        return null;
    }

    @Override
    public void printHelp() {

    }
}
