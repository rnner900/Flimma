package Flimma;

public class Parser {
    public static String[] parseArgs(String input) {
        return input.split(" ");
    }

    public static int parseInt(String value) throws NumberFormatException {
        return Integer.parseInt(value);
    }

    public static double parseDouble(String value) throws NumberFormatException {
        return Double.parseDouble(value);
    }
}
