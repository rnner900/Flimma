package Flimma.Pages;

import Flimma.Functions.InputException;
import Flimma.Models.Database;

public abstract class Page {

    protected final Database database;

    public Page(Database database) {
        this.database = database;
    }

    /**
     * Prints all content of this page (called by Application)
     */
    public abstract void show();


    /**
     * Processes the user input (called by Application)
     * @param input user input
     * @return next page to show
     * @throws InputException input invalid exception
     */
    public abstract Page onInput(String input) throws InputException;


    /**
     * Prints the help of this page (called by Application on '/help' input)
     */
    public abstract  void printHelp();
}
