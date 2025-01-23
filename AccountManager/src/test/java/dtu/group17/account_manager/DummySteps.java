package dtu.group17.account_manager;

import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DummySteps {

    @Then("is True")
    public void isTrue() {
        assertTrue(true);
    }

}
