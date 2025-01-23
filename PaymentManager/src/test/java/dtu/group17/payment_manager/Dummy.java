package dtu.group17.payment_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Then;

public class Dummy {
    @Then("assert {int} equals {int}")
    public void assertIntEqualsInt(Integer int1, Integer int2) {
        assertEquals(int1, int2);
    }
}