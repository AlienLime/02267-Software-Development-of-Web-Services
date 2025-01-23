Feature: tokenManager

  #RequestTokens
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
    And the "RequestTokensFailed" event is sent

  Scenario: Customer requests tokens while having too many already
    Given a registered customer with 3 tokens
    When the customer requests 2 tokens
    Then the customer has 3 tokens
    And the "RequestTokensFailed" event is sent

  Scenario: a Customer gets initialized
    When a customer is initialized
    Then that customer is in the token repository

  Scenario: A token is consumed
    Given a registered customer with 1 tokens
    When there is an attempt to consume the token
    Then the customer has 0 tokens
    And the "TokenConsumed" event is sent

  Scenario: An invalid token is attempted consumed
    When there is an attempt to consume an invalid token
    And the "TokenConsumptionFailed" event is sent

  Scenario: A token is validated
    Given a registered customer with 1 tokens
    And their token is consumed
    When token validation is attempted
    And the "TokenValidated" event is sent
    Then the customer is identified
    And the customer has 0 tokens

  Scenario: A token validation attempt without consuming the token
    Given a registered customer with 1 tokens
    When token validation is attempted
    And the "TokenValidationFailed" event is sent
    And the customer has 1 tokens

  Scenario: A token validation attempt with an invalid token
    Given a registered customer with 1 tokens
    When there is a token validation attempt with token id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99'
    And the "TokenValidationFailed" event is sent
    And the customer has 1 tokens

  Scenario: The token manager is cleared
    Given a registered customer with 2 tokens
    When the token manager is cleared
    Then the customer has 0 tokens

