Feature: Retrieve Bank Account
  Background: During the payment process we need to resolve the bank account ids of the customer and merchant involved in a payment

  Scenario: Request a customers bank account id
    Given there is a registered customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
    When the TokenValidated event for the customer is received
    Then the CustomerBankAccountRetrieved event is sent with a bank account id

  Scenario: Request a merchants bank account id
    Given there is a registered merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5'
    When the PaymentRequested event for the merchant is received
    Then the MerchantBankAccountRetrieved event is sent with a bank account id

  Scenario: Request a customers bank account id for a non-existent customer
    Given there is a customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
    When the TokenValidated event for the customer is received
    Then the "RetrieveCustomerBankAccountFailed" error event is sent with message "Customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' does not exist"

  Scenario: Request a merchants bank account id for a non-existent merchant
    Given there is a merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5'
    When the PaymentRequested event for the merchant is received
    Then the "RetrieveMerchantBankAccountFailed" error event is sent with message "Merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5' does not exist"
