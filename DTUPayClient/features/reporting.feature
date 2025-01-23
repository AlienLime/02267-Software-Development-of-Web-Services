Feature: Reporting
  Background: The three types of users can each generate a unique report with relevant payments

  Scenario: Customer requests report
    Given a registered customer with 2 token(s)
    And the customer has made the following payments
      | amount | merchant name         |
      | 10     | Group17-PF Caféen     |
      | 48     | Group17-Kantine 101   |
    When the customer request to receive their report
    Then the customer receives a report containing information of all the customer's payments

  Scenario: Customer with no payments requests report
    Given a registered customer
    When the customer request to receive their report
    Then the customer receives an empty report

  Scenario: Merchant requests report
    Given a registered merchant with a bank account
    And the following payments have been made to the merchant
      | amount |
      | 1000   |
      | 10     |
      | 250    |
    When the merchant request to receive their report
    Then the merchant receives a report containing information of all their received payments

  Scenario: Merchant with no payments requests report
    Given a registered merchant with a bank account
    When the merchant request to receive their report
    Then the merchant receives an empty report

  Scenario: Manager requests report
    Given the following payments have been made
      | amount | customer name      | merchant name       |
      | 10     | Group17-Katja Kaj  | Group17-Kantine 101 |
      | 22     | Group17-Bente Bent | Group17-Hegnet 358  |
    When the manager request to receive their report
    Then the manager receives a report containing information of all the payments

  Scenario: Manager with no payments requests report
    When the manager request to receive their report
    Then the manager receives an empty report

  Scenario: Customer requests their report concurrently
    Given a registered customer with 2 token(s)
    And the customer has made the following payments
      | amount | merchant name         |
      | 10     | Group17-PF Caféen     |
      | 48     | Group17-Kantine 101   |
    When the customer submits two requests to receive their report
    Then the customer receives two reports containing information of all the customer's payments

  Scenario: Merchant requests their report concurrently
    Given a registered merchant with a bank account
    And the following payments have been made to the merchant
      | amount |
      | 1000   |
      | 10     |
      | 250    |
    When the merchant submits two requests to receive their report
    Then the merchant receives two reports containing information of all the payments

  Scenario: Manager requests their report concurrently
    Given the following payments have been made
      | amount | customer name      | merchant name       |
      | 10     | Group17-Katja Kaj  | Group17-Kantine 101 |
      | 22     | Group17-Bente Bent | Group17-Hegnet 358  |
    When the manager submits two requests to receive their report
    Then the manager receives two reports containing information of all the payments

  Scenario: Payments make concurrently are reported
    Given the following payments have been submitted concurrently
      | amount | customer name      | merchant name       |
      | 10     | Group17-Katja Kaj  | Group17-Kantine 101 |
      | 22     | Group17-Bente Bent | Group17-Hegnet 358  |
    When the manager request to receive their report
    Then the manager receives a report containing information of all the payments
