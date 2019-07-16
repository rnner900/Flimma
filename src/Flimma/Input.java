package Flimma;

import Flimma.Functions.InputException;

public class Input {
    private final String cmd;
    private final String[] args;
    private final String input;

    public Input(String input) {
        args = input.split(" ");
        cmd = args[0];
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public String getCmd() {
        return cmd;
    }

    public int argToInt(int index) throws InputException {
        try {
            return Integer.parseInt(args[index]);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    public double argToDouble(int index) throws InputException {
        try {
            return Double.parseDouble(args[index]);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    public String argToString(int index) throws InputException {
        try {
            return args[index];
        } catch (Exception e) {
            throw new InputException();
        }
    }
}
