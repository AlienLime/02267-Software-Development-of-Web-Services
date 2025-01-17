Feature: Full Payment
  Scenario: Successful Payment
    Given a customer with name "Group17-Katja", last name "Group17-Kaj", and CPR "170155-4452"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    # have at least one unused token
    And the customer has 5 unused tokens
    Given a merchant with name "Group17-Bente", last name "Group17-Bent", and CPR "171161-3052"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant creates a payment for 10 kr
    And the customer presents a valid token to the merchant
    And the merchant receives the token
    And the merchant submits the transaction to the server

    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr

# Make a test to check when a token is consumed#

# Make a test for reporting, for manager, customer and merchant