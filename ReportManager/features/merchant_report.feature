Feature: Manager Report

  Background: A merchant should be able to request a report over their received payments

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
