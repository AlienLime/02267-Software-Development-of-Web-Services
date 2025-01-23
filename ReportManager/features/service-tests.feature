Feature: Service Level Tests

  Scenario: Payment is added to repository
    When a payment is completed
    Then the payment is added to the repository

  Scenario: Customer requests a report
    Given 3 payments made by a customer
    When the customer requests a report
    Then a customer report generated event with the report is sent

  Scenario: Customer does not see other customers payments in a report
    Given a customer with id "9de1b42d-2827-47e6-8527-f79706cac0f4"
    And a customer with id "a63d42f2-9a8f-42b2-bfaf-49e536d13b87"
    And 3 payments made by that customer
    When the customer with id "9de1b42d-2827-47e6-8527-f79706cac0f4" requests a report
    Then a customer report generated event with an empty report is sent

  Scenario: Merchant requests a report
    Given 3 payments made to a merchant
    When the merchant requests a report
    Then a merchant report generated event with the report is sent

  Scenario: Merchant does not see other merchants payments in a report
    Given a merchant with id "9de1b42d-2827-47e6-8527-f79706cac0f4"
    And a merchant with id "a63d42f2-9a8f-42b2-bfaf-49e536d13b87"
    And 3 payments made to that merchant
    When the merchant with id "9de1b42d-2827-47e6-8527-f79706cac0f4" requests a report
    Then a merchant report generated event with an empty report is sent

  Scenario: Manager requests a report
    Given 3 payments made
    When the manager requests a report
    Then a manager report generated event with the report is sent

  Scenario: Reports are cleared
    Given 3 payments made
    When the reports are cleared
    And the manager requests a report
    Then a manager report generated event with an empty report is sent