package dtu.group17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StringCalculator {
    public int add(String input) throws Exception {
        if (input.isBlank()) return 0;

        String[] numbers;
        if (input.startsWith("//")) {
            StringBuilder delimiter = new StringBuilder();
            for (int i = 2; input.charAt(i) != '\n'; i++) {
                delimiter.append(input.charAt(i));
            }
            numbers = input.split("\n|" + delimiter);
            numbers =  Arrays.copyOfRange(numbers, 2, numbers.length);
        } else {
            numbers = input.split("[\n,]");
        }

        int sum = 0;
        List<String> negatives = new ArrayList<>();
        for (String number : numbers) {
            int n = Integer.parseInt(number);
            if (n < 0) {
                negatives.add(number);
            }else {
                sum += n;
            }
        }

        if (!negatives.isEmpty()) {
            throw new Exception("negatives not allowed: " + String.join(",", negatives));
        }

        return sum;
    }
}
