package Flimma.Page;

public interface Page {

    void show();

    Page onInput(String input) throws Exception;

    void printHelp();
}
