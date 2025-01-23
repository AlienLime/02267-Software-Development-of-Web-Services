Feature: Manager Report

  Background: The manager should be able to request a report over all payments through DTU Pay

  Scenario: Manager requests a report
    Given 3 payments made
    When the manager requests a report
    Then a manager report generated event with the report is sent