package dtu.group17;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class AddSteps {
    StringCalculator stringCalculator = new StringCalculator();
    String input;
    int result;
    String errorMessage;

    @Given("the string is empty")
    public void theStringIsEmpty() {
        input = "";
    }

    @Given("the string is {string}")
    public void the_string_is(String input) {
        this.input = input.replace("\\n", "\n"); // section 1.4.3
    }

    @When("add is called")
    public void addIsCalled() {
        try {
            result = stringCalculator.add(input);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
    }

    @Then("{int} is returned")
    public void isReturned(int expectedNumber) {
        Assertions.assertEquals(expectedNumber, result);
    }

    @Then("the following error message is returned:")
    public void the_following_error_message_is_returned(String errorMessage) {
        Assertions.assertEquals(errorMessage, this.errorMessage);
    }
}
