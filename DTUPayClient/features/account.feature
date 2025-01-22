Feature: Accounts
  Scenario: Customer is successfully registered
    Given a customer with name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-2344"
    And the customer is registered with the bank
    When the customer tries to register with DTU Pay using their bank account
    Then the customer is registered successfully and with the name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-2344"

  Scenario: Merchant is successfully registered
    Given a merchant with name "Group17-Daniel", last name "Group17-Mars", and CPR "171161-3045"
    And the merchant is registered with the bank
    When the merchant tries to register with DTU Pay using their bank account
    Then the merchant is registered successfully and with the name "Group17-Daniel", last name "Group17-Mars", and CPR "171161-3045"

  Scenario: Customer is successfully deregistered
    Given a registered customer with cpr "170154-2344"
    When the customer tries to deregister their account from DTU Pay
    Then the customer with cpr "170154-2344" is successfully deregistered

  Scenario: Merchant is successfully deregistered
    Given a registered merchant with cpr "170154-2344"
    When the merchant tries to deregister their account from DTU Pay
    Then the merchant with cpr "170154-2344" is successfully deregistered
