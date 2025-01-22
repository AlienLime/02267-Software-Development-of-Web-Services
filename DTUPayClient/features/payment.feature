Feature: Payment
  Background: Payments can be made from a customer to a merchants registered with DTU Pay

  Scenario: Successful Payment
    Given a registered customer with a balance of 1000 kr and 5 token(s)
    And a registered merchant with a bank account and a balance of 1000 kr
    When the merchant creates a payment for 10 kr
    And the customer presents a valid token to the merchant
    And the merchant receives the token
    And the merchant submits the payment
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr

  Scenario: Payment with invalid merchant id
    Given a registered customer with 1 token(s)
    When a payment is created with merchant id "497dcba3-ecbf-4587-a2dd-5eb0665e6880"
    And the customer presents a valid token to the merchant
    And the merchant receives the token
    And the merchant submits the payment
    Then the payment is unsuccessful
    And the error message is "Merchant with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' does not exist"

  Scenario: Customer has insufficient funds
    Given a registered customer with a balance of 0 kr and 1 token(s)
    And a registered merchant with a bank account and a balance of 1000 kr
    When a payment of 10 kr between the customer and merchant is submitted
    Then the payment is unsuccessful
    And the error message is "Debtor balance will be negative"
    And the balance of the customer at the bank is 0 kr
    And the balance of the merchant at the bank is 1000 kr

  Scenario: Customer with bad account id
    Given a customer who is not registered with the bank
    And a registered merchant with a bank account and a balance of 1000 kr
    When a payment of 10 kr between the customer and merchant is submitted
    Then the payment is unsuccessful
    And the error message is "Debtor account does not exist"

  Scenario: Merchant with unknown bank account
    Given a registered customer with 1 token(s)
    And a merchant who is not registered with the bank
    When a payment of 10 kr between the customer and merchant is submitted
    Then the payment is unsuccessful
    And the error message is "Creditor account does not exist"

  Scenario: Concurrent payments to merchant
    Given two registered customers each with a balance of 1000 kr and 1 token(s)
    And a registered merchant with a bank account and a balance of 1000 kr
    When the merchant submits a payment of 50 kr for each customer at the same time
    Then the balance of both customers at the bank is 950 kr
    And the balance of the merchant at the bank is 1100 kr

  Scenario: Concurrent payments by a customer
    Given two registered merchants each with a balance of 10 kr
    And a registered customer with a balance of 100 kr and 2 token(s)
    When both merchants submit a payment of 50 kr to the customer
    Then the balance of the customer at the bank is 0 kr
    And the balance of both merchants at the bank is 60 kr

  Scenario: Concurrent payments by a customer with insufficient funds
    Given two registered merchants each with a balance of 10 kr
    And a registered customer with a balance of 50 kr and 2 token(s)
    When both merchants submit a payment of 50 kr to the customer
    Then the balance of the customer at the bank is 0 kr
    And one of the two merchants balance at the bank is 60 kr
    And the error message is "Debtor balance will be negative"
