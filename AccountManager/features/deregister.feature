Feature: Deregister
  Background: Once a customer or merchant is registered with DTU Pay they can also deregister

  Scenario: Customer is successfully deregistered
    Given there is a registered customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
    When the CustomerDeregistrationRequested event is received
    Then the "CustomerDeregistered" confirmation event is sent
    And the customer is deregistered

  Scenario: Merchant is successfully deregistered
    Given there is a registered merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5'
    When the MerchantDeregistrationRequested event is received
    Then the "MerchantDeregistered" confirmation event is sent
    And the merchant is deregistered

  Scenario: Unregistered customer attempts deregistration
    Given there is a customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
    When the CustomerDeregistrationRequested event is received
    Then the "DeregisterCustomerFailed" error event is sent with message "Customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' does not exist"

  Scenario: Unregistered merchant attempts deregistration
    Given there is a merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5'
    When the MerchantDeregistrationRequested event is received
    Then the "DeregisterMerchantFailed" error event is sent with message "Merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5' does not exist"
