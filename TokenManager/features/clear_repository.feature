Feature: Clear Repository

  Background: The manager can remove all tokens from DTU Pay

  Scenario: The token manager is cleared
    Given a registered customer with 2 tokens
    When the token manager is cleared
    Then the customer has 0 tokens
    And the token repository is empty