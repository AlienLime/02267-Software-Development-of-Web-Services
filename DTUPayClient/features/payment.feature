Feature: Payment
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

