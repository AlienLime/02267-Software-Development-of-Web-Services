Feature: Full Payment
  Scenario: Successful Payment
    Given a customer with name "Group17-Katja", last name "Group17-Kaj", and CPR "170155-0059"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 5 unused tokens
    Given a merchant with name "Group17-Bente", last name "Group17-Bent", and CPR "171161-0059"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant creates a payment for 10 kr
    And the customer presents a valid token to the merchant
    And the merchant receives the token
    And the merchant submits the transaction to the server
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr

  Scenario: Payment with invalid merchant id
    Given a registered customer with 1 token(s)
    When a payment is created with merchant id "non-existent-merchant"
    And the customer presents a valid token to the merchant
    And the merchant receives the token
    And the merchant submits the transaction to the server
    Then the payment is unsuccessful
    And the error message is "Merchant with id 'non-existent-merchant' does not exist"

  Scenario: Customer has insufficient funds
    Given a registered customer with 0 kr and 1 token(s)
    And a registered merchant with 1000 kr
    When a payment of 10 kr between the customer and merchant is submitted
    Then the payment is unsuccessful
    And the error message is "Customer has insufficient funds"
    And the balance of the customer at the bank is 0 kr
    And the balance of the merchant at the bank is 1000 kr