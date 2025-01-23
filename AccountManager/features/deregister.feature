Feature: Deregister
  @Ignore
  Scenario: Customer is successfully deregistered
  Given a registered customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
  When the customer tries to deregister their account from DTU Pay
  Then the customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' is successfully deregistered

  @Ignore
  Scenario: Merchant is successfully deregistered
  Given a registered merchant with id '5516e359-6c9c-4ebb-a409-52373d536d50'
  When the merchant tries to deregister their account from DTU Pay
  Then the merchant with id '5516e359-6c9c-4ebb-a409-52373d536d50' is successfully deregistered
