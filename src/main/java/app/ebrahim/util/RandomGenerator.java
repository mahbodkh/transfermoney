package app.ebrahim.util;

import java.util.Random;
import java.util.UUID;

public class RandomGenerator {

    public static String iban() {
        {
            String start = "UK";
            Random value = new Random();
            int r1 = value.nextInt(10);
            int r2 = value.nextInt(10);
            start += Integer.toString(r1) + Integer.toString(r2) + "-";
            int count = 0;
            int n = 0;
            for (int i = 0; i < 12; i++) {
                if (count == 4) {
                    start += "-";
                    count = 0;
                } else
                    n = value.nextInt(10);
                start += Integer.toString(n);
                count++;

            }
            return start;
        }
    }

    public static String stan() {
        return UUID.randomUUID().toString();
    }


}
