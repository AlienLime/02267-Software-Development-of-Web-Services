Feature: Payment
  Scenario: Successful Payment
    Given a customer with name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-4421"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    Given a merchant with name "Group17-Daniel", last name "Group17-Oliver", and CPR "171161-3045"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with Simple DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr

  Scenario: List of payments
    Given a customer with name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-4421"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    Given a merchant with name "Group17-Daniel", last name "Group17-Oliver", and CPR "171161-3045"
    And the merchant is registered with Simple DTU Pay using their bank account
    And a successful payment of 10 kr from the customer to the merchant
    When the manager asks for a list of payments
    Then the list contains a payments where customer "Group17-Susan" paid 10 kr to merchant "Group17-Daniel"
#
#  Scenario: Customer is not known
#    Given a merchant with name "Group17-Daniel", last name "Group17-Oliver", and CPR "171161-3045"
#    And the merchant is registered with the bank with an initial balance of 1000 kr
#    And the merchant is registered with Simple DTU Pay using their bank account
#    When the merchant initiates a payment for 10 kr using customer id "non-existent-id"
#    Then the payment is not successful
#    And an error message is returned saying "customer with id \"non-existent-id\" is unknown"
#
#  Scenario: Merchant is not known
#    Given a customer with name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-4421"
#    And the customer is registered with the bank with an initial balance of 1000 kr
#    And the customer is registered with Simple DTU Pay using their bank account
#    When the merchant with id "non-existent-id" initiates a payment for 10 kr using the customer
#    Then the payment is not successful
#    And an error message is returned saying "merchant with id \"non-existent-id\" is unknown"