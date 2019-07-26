package Flimma;

import Flimma.Functions.InputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper class to get user input from the console
 */
public class Input {

    public static int inputInt(String question) throws InputException {

        String input = inputStr(question);
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    public static double inputDouble(String question) throws InputException {

        String input = inputStr(question);
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            throw new InputException();
        }
    }

    public static String inputStr(String question) {

        System.out.println();
        System.out.println(question);
        System.out.print("> ");
        return inputStr();

    }

    public static String inputStr() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
