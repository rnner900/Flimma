package Flimma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Input {

    public static String waitForInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }
}
