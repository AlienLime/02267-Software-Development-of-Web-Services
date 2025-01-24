Feature: Manage Customers

  Background: When a customer registers with DTU Pay, they are added Token Manager. Similarly, they deregister

  Scenario: a Customer is registered
    When a customer is initialized
    Then that customer is in the token repository

  Scenario: a Customer is deregistered
    When a customer is removed
    Then that customer is no longer in the token repository
