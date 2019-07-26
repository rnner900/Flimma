package Flimma;

/**
 * Helper class to print tables to the console
 */
public class Table {

    private final String[] formats;

    /**
     * Creates table with multiple columns by a given format for printing.
     * Example: new Table("-20s.20s", "-30s.30s");
     * will create a table left-aligned (width column1: 20 chars, width column2: 30 chars)
     * @param formats table colmn formats
     */
    public Table(String... formats) {
        this.formats = formats;
    }

    /**
     * Prints a table line through all columns
     */
    public void printLine() {
        System.out.print("+");
        for (String format : formats) {
            System.out.print(String.format(format, "---------------------------------------------------------"));
            System.out.print("--");
            System.out.print("+");
        }
        System.out.println();
    }

    /**
     * Prints a table line with labels through all columns
     * @param labels labels for the columns
     */
    public void printLine(String... labels) {
        System.out.print("+");
        for (int i = 0; i < formats.length; i++) {
            String format = formats[i];
            System.out.print("-");
            System.out.print(String.format(format, labels[i] + "--------------------------------------------------------"));
            System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
    }

    /**
     * Prints a row with content
     * @param contents content of column
     */
    public void printRow(Object... contents) {
        System.out.print("|");
        for (int i = 0; i < contents.length; i++) {
            System.out.print(" ");

            String format = formats[i];
            String content = contents[i].toString();
            String sliced = String.format(format, content);
            if (content.length() > sliced.length()) {
                sliced = sliced.substring(0, sliced.length()-3) + "...";
            }

            System.out.print(sliced);

            System.out.print(" |");
        }
        System.out.println();
    }
}
