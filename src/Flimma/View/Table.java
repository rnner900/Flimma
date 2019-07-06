package Flimma.View;

public class Table {

    private String[] formats;

    public Table(String... formats) {
        this.formats = formats;
    }

    public void printLine() {
        System.out.print("+");
        for (int i = 0; i < formats.length; i++) {
            String format = formats[i];
            System.out.print(String.format(format, "---------------------------------------------------------"));
            System.out.print("--");
            System.out.print("+");
        }
        System.out.println();
    }
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

    public void printColumnB(Object... contents){
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

    public void printColumn(Object... contents) {
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
