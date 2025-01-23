Feature: Clear Repository
  Background: The manager can remove all accounts from DTU Pay

    Scenario:
      When the ClearRequested event is received
      Then the "AccountsCleared" confirmation event is sent
      And the account repository is empty
