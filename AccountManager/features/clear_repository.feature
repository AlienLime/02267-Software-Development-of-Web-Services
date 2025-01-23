Feature: Clear Repository
  Background: The manager can remove all accounts from DTU Pay

    Scenario:
      Given there is a registered customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
      Given there is a registered merchant with id '72c5e147-c5d8-4840-8787-6f8637e537b5'
      When the ClearRequested event is received
      Then the "AccountsCleared" confirmation event is sent
      And the account repository is empty
