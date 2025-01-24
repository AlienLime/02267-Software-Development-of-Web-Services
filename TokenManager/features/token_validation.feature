Feature: Token Validation

  Background: When making a payment DTU pay validates the token belongs to a customer

  Scenario: A token is validated
    Given a registered customer with 1 tokens
    And their token has been consumed
    When token validation is attempted
    And the "TokenValidated" event is sent
    Then the customer is identified
    And the customer has 0 tokens

  Scenario: A token validation attempt without first having consumed the token
    Given a registered customer with a token with id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99'
    When token validation is attempted
    And the "TokenValidationFailed" event is sent with error message "Token with id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99' not found"
    And the customer has 1 tokens

  Scenario: A token validation attempt with an unknown token
    Given a registered customer with 1 tokens
    When there is a token validation attempt with token id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99'
    And the "TokenValidationFailed" event is sent with error message "Token with id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99' not found"
    And the customer has 1 tokens
