package dtu.group17;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringCalculatorTests {
    private final StringCalculator stringCalculator = new StringCalculator();

    @Test
    public void testAddNothing() throws Exception {
        assertEquals(0, stringCalculator.add(""));
    }

    @Test
    public void testAddOneNumber() throws Exception {
        assertEquals(1, stringCalculator.add("1"));
    }

    @Test
    public void testAddTwoNumbers() throws Exception{
        assertEquals(3, stringCalculator.add("1,2"));
    }

    @Test
    public void testAddThreeNumbers() throws Exception{
        assertEquals(6, stringCalculator.add("1,2,3"));
    }

    @Test
    public void testAddNewline() throws Exception {
        assertEquals(6, stringCalculator.add("1\n2,3"));
    }

    @Test
    public void testAddDelimiter() throws Exception {
        assertEquals(3, stringCalculator.add("//;\n1;2"));
    }

    @Test
    public void testAddNegative() {
        Assertions.assertThrows(Exception.class, () -> stringCalculator.add("-1,2,-2"), "negatives not allowed: -1,-2");
    }
}
