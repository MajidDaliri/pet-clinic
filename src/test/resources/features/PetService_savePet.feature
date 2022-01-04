Feature: save pet
  Background: Sample General Preconditions Explanation
    Given There is a pet type like "cat"

  Scenario: majid wants to save a pet
    Given Majid is an owner
    When Majid performs save pet service to add a pet to his list
    Then The pet is saved

  Scenario: majid buys a pet
    Given Majid is an owner
    When Majid asks for a new pet
    And Majid performs save pet service to add a pet to his list
    Then A new pet is given to majid
    And The pet is saved
