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


    /**
     * gets the nth argument and parses it to an integer
     * @param n
     * @return parsed integer
     * @throws InputException thrown if input is invalid (parse or out of range error)
     */
    public int argToInt(int n) throws InputException {
        try {
            return Integer.parseInt(args[n]);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    /**
     * gets the nth argument and parses it to an double
     * @param n
     * @return parsed double
     * @throws InputException thrown if input is invalid (parse or out of range error)
     */
    public double argToDouble(int n) throws InputException {
        try {
            return Double.parseDouble(args[n]);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    /**
     * gets the nth argument
     * @param n
     * @return string
     * @throws InputException thrown if input is invalid (out of range error)
     */
    public String argToString(int n) throws InputException {
        try {
            return args[n];
        } catch (Exception e) {
            throw new InputException();
        }
    }
}
