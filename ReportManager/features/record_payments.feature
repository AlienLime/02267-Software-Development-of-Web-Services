Feature: Record Payments

  Background: The report manager should keep track of all payments made to provide reports

  Scenario: Payment is added to repository
    When a payment is completed
    Then the payment is added to the repository
