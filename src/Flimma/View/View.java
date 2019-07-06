package Flimma.View;

public abstract class View {

    public abstract void show();

    public abstract View onInput(String input) throws Exception;

    public abstract void printHelp();
}
