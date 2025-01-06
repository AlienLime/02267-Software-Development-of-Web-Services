Feature: String Calculator Add
  Background:
    Actors: User
    Rule: The calculator adds an unlimited amount of positive numbers

  Scenario: Empty string
    Given the string is empty
    When add is called
    Then 0 is returned

  Scenario: String of 1 number
    Given the string is "1"
    When add is called
    Then 1 is returned

  Scenario: String of 2 numbers
    Given the string is "1,2"
    When add is called
    Then 3 is returned

  Scenario: String of 3 numbers
    Given the string is "1,2,3"
    When add is called
    Then 6 is returned

  Scenario: String with newline delimiters
    Given the string is "1\n2,3"
    When add is called
    Then 6 is returned

  Scenario: String with custom delimiters
    Given the string is "//;\n1;2"
    When add is called
    Then 3 is returned

  Scenario: String with negative numbers
    Given the string is "-1,2,-2"
    When add is called
    Then the following error message is returned:
      """
      negatives not allowed: -1,-2
      """