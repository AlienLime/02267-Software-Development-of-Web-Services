Feature: Customer Reports

  Background: A customer should be able to request a report over the payments they have made

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
