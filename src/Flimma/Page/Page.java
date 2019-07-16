package Flimma.Page;

import Flimma.Functions.InputException;
import Flimma.Input;
import Flimma.Model.Database;


public abstract class Page {

    protected final Database database;

    public Page(Database database) {
        this.database = database;
    }

    public abstract void show();

    public abstract Page onInput(Input input) throws InputException;

    public abstract  void printHelp();
}
