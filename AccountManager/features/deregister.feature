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

  @Ignore
  Scenario: The same customer deregisters concurrently
    Given a registered customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880'
    When the customer submits two deregistration requests
    Then the customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' is successfully deregistered
    And the error message is "Customer with id '497dcba3-ecbf-4587-a2dd-5eb0665e6880' does not exist"

  @Ignore
  Scenario: The same merchant deregisters concurrently
    Given a registered merchant with id '5516e359-6c9c-4ebb-a409-52373d536d50'
    When the merchant submits two deregistration requests
    Then the merchant with id '5516e359-6c9c-4ebb-a409-52373d536d50' is successfully deregistered
    And the error message is "Merchant with id '5516e359-6c9c-4ebb-a409-52373d536d50' does not exist"
