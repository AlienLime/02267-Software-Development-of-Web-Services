Feature Token Handling
  # This feature tests that everything works as intended in regards to the use of customer tokens

  Scenario: Correct amount of tokens distributed upon request
    Given a registered customer with 0 token(s)
    When the customer requests 2 tokens
    Then the customer's number of tokens is 2

  Scenario: When a customer consumes a token, that token is consumed
    Given a registered customer with 2 token(s)
    When the customer presents a valid token to the merchant
    Then the token is consumed

  Scenario: Payment fails because token does not exist
    Given a registered merchant
    When the merchant creates a payment
    And the merchant receives an invalid token
    And the merchant submits the transaction to the server
    Then the payment is unsuccessful
    And the error message is "Invalid token"

  Scenario: Customer requests tokens while having more than one remaining
    Given a registered customer with 2 token(s)
    When the customer requests 2 tokens
    Then the error message is "Cannot request new tokens when you have 2 or more tokens"

  Scenario: Upon account creation the account has zero tokens
    When a customer is registered
    Then the customer's number of tokens is 0

  Scenario: Customer requests 0 tokens
    Given a registered customer with 1 token(s)
    When the customer requests 0 tokens
    Then the error message is "Only 1-5 tokens can be requested"

  Scenario: Customer requests above 5 tokens
    Given a registered customer with 1 token(s)
    When the customer requests 6 tokens
    Then the error message is "Only 1-5 tokens can be requested"
