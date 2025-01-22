Feature: Token Handling
  Background: A customer can request tokens to make future payments with

  Scenario: Correct amount of tokens distributed upon request
    Given a registered customer with 0 token(s)
    When the customer requests 2 token(s)
    Then the customer received 2 token(s)

  Scenario: Payment fails because token does not exist
    Given a registered merchant with a bank account
    When the merchant creates a payment
    And the merchant receives a token with id '14ff4718-1261-4b95-907c-c99ce807c318'
    And the merchant submits the payment
    Then the payment is unsuccessful
    And the error message is "Token with id '14ff4718-1261-4b95-907c-c99ce807c318' not found"

  Scenario: Customer requests tokens while having more than one remaining
    Given a registered customer with 2 token(s)
    When the customer requests 2 token(s)
    Then the error message is "Cannot request new tokens when you have 2 or more tokens"

  Scenario: Customer requests 0 tokens
    Given a registered customer with 1 token(s)
    When the customer requests 0 token(s)
    Then the error message is "Only 1-5 tokens can be requested"

  Scenario: Customer requests above 5 tokens
    Given a registered customer with 1 token(s)
    When the customer requests 6 token(s)
    Then the error message is "Only 1-5 tokens can be requested"

  Scenario: Concurrent token requests by a customer
    Given a registered customer with 0 token(s)
    When the customer submits two requests for 1 token
    Then the customer received 2 token(s)
