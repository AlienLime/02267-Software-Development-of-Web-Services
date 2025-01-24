Feature: Consume Tokens

  Background: When the customer presents their token via. RFID it is consumed

  Scenario: A token is consumed
    Given a registered customer with 1 tokens
    When there is an attempt to consume the token
    Then the customer has 0 tokens
    And the "TokenConsumed" event is sent

  Scenario: An invalid token is attempted consumed
    When there is an attempt to consume an invalid token with id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99'
    Then the "TokenConsumptionFailed" event is sent with error message "Token with id 'c40c4c86-4b66-43ee-8050-52c0b2ecbb99' not found"
