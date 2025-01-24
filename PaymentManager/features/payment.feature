# Created by emilk at 23/01/2025
Feature: Payment
  # Enter feature description here

  Scenario: Transaction between bank accounts successful
    Given the payment data with sufficient funds has been submitted
    When the payment is processed
    Then the PaymentCompleted event is published with correct data

  Scenario: Transaction failure due to low balance
    Given the payment data with insufficient funds has been submitted
    When the payment is processed
    Then the "PaymentFailed" event is published with error message "Debtor balance will be negative"

  Scenario: Transaction failure due to invalid merchant account
    Given the payment data with invalid merchant account has been submitted
    When the payment is processed
    Then the "PaymentFailed" event is published with error message "Creditor account does not exist"

  Scenario: Transaction failure due to invalid customer account
    Given the payment data with invalid customer account has been submitted
    When the payment is processed
    Then the "PaymentFailed" event is published with error message "Debtor account does not exist"

  Scenario: PaymentRequested event is successfully processed
    Given a PaymentRequested event with valid data is received
    When the PaymentRequested event is processed
    Then the payment data is correctly stored in the paymentDatas map

  Scenario: CustomerBankAccountRetrieved event is successfully processed
    Given a CustomerBankAccountRetrieved event with valid data is received
    When the CustomerBankAccountRetrieved event is processed
    Then the customer account data is correctly updated in the paymentDatas map

  Scenario: MerchantBankAccountRetrieved event is successfully processed
    Given a MerchantBankAccountRetrieved event with valid data is received
    When the MerchantBankAccountRetrieved event is processed
    Then the merchant account data is correctly updated in the paymentDatas map

  Scenario: Successful processing of events
    Given "CustomerBankAccountRetrieved", "MerchantBankAccountRetrieved", and "PaymentRequested" are received with valid data
    When all events are processed in the given order "CustomerBankAccountRetrieved", "MerchantBankAccountRetrieved", and "PaymentRequested"
    Then the PaymentCompleted event is published with correct data

  Scenario: Successful processing of events
    Given "PaymentRequested", "CustomerBankAccountRetrieved", and "MerchantBankAccountRetrieved" are received with valid data
    When all events are processed in the given order "PaymentRequested", "CustomerBankAccountRetrieved", and "MerchantBankAccountRetrieved"
    Then the PaymentCompleted event is published with correct data

  Scenario: Successful processing of events
    Given "MerchantBankAccountRetrieved", "PaymentRequested", and "CustomerBankAccountRetrieved" are received with valid data
    When all events are processed in the given order "MerchantBankAccountRetrieved", "PaymentRequested", and "CustomerBankAccountRetrieved"
    Then the PaymentCompleted event is published with correct data