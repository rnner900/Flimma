package Flimma.Page;

public class Table {

    private final String[] formats;

    /**
     * Creates table with multiple columns by a given format.
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
     * Prints a row with content in bold
     * @param contents content of column
     */
    public void printRowB(Object... contents){
        System.out.print("|");
        for (int i = 0; i < contents.length; i++) {

            System.out.print("\033[0;1m"); // print bold

            System.out.print(" ");
            String format = formats[i];
            Object content = contents[i];
            System.out.print(String.format(format, content));

            System.out.print("\033[0m"); // print normal

            System.out.print(" |");
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
            Object content = contents[i];
            System.out.print(String.format(format, content));
            System.out.print(" |");
        }
        System.out.println();
    }
}
