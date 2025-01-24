Feature: Request Tokens

  Background: A customer can request tokens to be able to make payments

  Scenario: Customer successfully requests a token
    Given a registered customer with 1 tokens
    When the customer requests 1 tokens
    Then the customer has 2 tokens
    And the "TokensGenerated" event is sent
    And the event contains 1 token

  Scenario: Customer requests too many tokens
    Given a registered customer with 1 tokens
    When the customer requests 17 tokens
    Then the customer has 1 tokens
    And the "RequestTokensFailed" event is sent with error message "Only 1-5 tokens can be requested"

  Scenario: The Customer requests too few tokens
    Given a registered customer with 1 tokens
    When the customer requests 0 tokens
    Then the customer has 1 tokens
    And the "RequestTokensFailed" event is sent with error message "Only 1-5 tokens can be requested"

  Scenario: Customer requests tokens while having too many already
    Given a registered customer with 3 tokens
    When the customer requests 2 tokens
    Then the customer has 3 tokens
    And the "RequestTokensFailed" event is sent with error message "Cannot request new tokens when you have 2 or more tokens"
