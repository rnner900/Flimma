package Flimma.Models;

public class Director {
    private final String name;

    public Director(String name) {
        this.name = name;
    }

    // getter methods
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
