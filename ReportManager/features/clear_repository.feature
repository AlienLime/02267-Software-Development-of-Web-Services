Feature: Clear Repository

  Background: The manager can remove all reports from DTU Pay

  Scenario: Reports are cleared
    Given 3 payments made
    When the reports are cleared
    And the manager requests a report
    Then a manager report generated event with an empty report is sent