Feature: Reporting
  # Enter feature description here

  @Ignore
  Scenario: Customer Report
    Given a registered customer
    And the customer has made the following payments
      | amount | merchant name         |
      | 10     | Group17-PF Caféen     |
      | 48     | Group17-Kantine 101   |
    When the customer request to receive a report
    Then the customer receives a report containing information of all the customer's payments
    And the report includes the amount of money transferred, the merchants' names

  @Ignore
  Scenario: Merchant Report
    Given a registered merchant
    And the following payments have been made to the merchant
      | amount |
      | 1000   |
      | 10     |
      | 250    |
    When the merchant request to receive a report
    Then the merchant receives a report containing information of all their received payments
    And the report includes the amount of money transferred

  @Ignore
  Scenario: Manager Report
    Given a manager
    And the following payments have been made
      | amount | customer name      | merchant name       |
      | 10     | Group17-Katja Kaj  | Group17-Kantine 101 |
      | 22     | Group17-Bente Bent | Group17-Hegnet      |
    When the manager request to receive a report
    Then the manager receives a report containing information of all the payments
    And the report includes a summary of all money transferred
