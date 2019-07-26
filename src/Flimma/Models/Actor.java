package Flimma.Models;

public class Actor {
    private final String name;

    public Actor(String name) {
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
