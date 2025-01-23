Feature: Account
  Background: Users can register and deregister as customers and merchants

  Scenario: Customer is successfully registered
    Given a customer with name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-4949"
    And the customer is registered with the bank
    When the customer tries to register with DTU Pay using their bank account
    Then the customer is registered successfully and with the name "Group17-Susan", last name "Group17-Baldwin", and CPR "170154-4949"

  Scenario: Merchant is successfully registered
    Given a merchant with name "Group17-Daniel", last name "Group17-Mars", and CPR "171161-3045"
    And the merchant is registered with the bank
    When the merchant tries to register with DTU Pay using their bank account
    Then the merchant is registered successfully and with the name "Group17-Daniel", last name "Group17-Mars", and CPR "171161-3045"

  Scenario: Customer is successfully deregistered
    Given a registered customer with cpr "170154-4949"
    When the customer tries to deregister their account from DTU Pay
    Then the customer with cpr "170154-4949" is successfully deregistered

  Scenario: Merchant is successfully deregistered
    Given a registered merchant with cpr "170154-4949"
    When the merchant tries to deregister their account from DTU Pay
    Then the merchant with cpr "170154-4949" is successfully deregistered

  Scenario: Customer could not be deregistered
    When a customer with id "885fa908-ed18-4dde-a376-32ff921b8783" tries to deregister their account from DTU Pay
    Then the customer could not be deregistered
    And the error message is "Customer with id '885fa908-ed18-4dde-a376-32ff921b8783' does not exist"

  Scenario: Merchant could not be deregistered
    When a merchant with id "f0ab14ef-6cdc-4c1e-ae52-04de6c844dbc" tries to deregister their account from DTU Pay
    Then the merchant could not be deregistered
    And the error message is "Merchant with id 'f0ab14ef-6cdc-4c1e-ae52-04de6c844dbc' does not exist"

  Scenario: Two customers register concurrently
    Given two customers with a bank account and cpr numbers "170154-4949" "170154-2840"
    When both customers register with DTU Pay
    Then the customers with cpr numbers "170154-4949" and "170154-2840" are successfully registered

  Scenario: Two merchants register concurrently
    Given two merchants with a bank account and cpr numbers "170154-4949" "170154-2840"
    When both merchants register with DTU Pay
    Then the merchants with cpr numbers "170154-4949" and "170154-2840" are successfully registered

  Scenario: Two customers deregister concurrently
    Given two registered customers with cpr numbers "170154-4949" "170154-2840"
    When both customers deregister from DTU Pay
    Then the customers with cpr numbers "170154-4949" and "170154-2840" are successfully deregistered

  Scenario: Two merchants deregister concurrently
    Given two registered merchants with cpr numbers "170154-4949" "170154-2840"
    When both merchants deregister from DTU Pay
    Then the merchants with cpr numbers "170154-4949" and "170154-2840" are successfully deregistered
