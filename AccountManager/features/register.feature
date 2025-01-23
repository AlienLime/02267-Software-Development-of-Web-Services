Feature: Register
  Background: Customers and merchants can register with DTU Pay using their firstName, lastName and CPR

  Scenario: Customer is successfully registered
    Given there is a customer with empty id
    When the CustomerRegistrationRequested event is received
    Then the CustomerRegistered event is sent with a non-empty id
    And the customer is registered with a non-empty id

  Scenario: Merchant is successfully registered
    Given there is a merchant with empty id
    When the MerchantRegistrationRequested event is received
    Then the MerchantRegistered event is sent with a non-empty id
    And the merchant is registered with a non-empty id
