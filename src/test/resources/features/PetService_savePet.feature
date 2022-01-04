Feature: save pet
  Scenario: majid wants to save a pet
    Given Majid is an owner
    When He performs save pet service to add a pet to his list
    Then The pet is saved successfully

  Scenario: majid buys a pet
    Given Majid is an owner
    When Majid asks for a new pet
    And He performs save pet service to add a pet to his list
    Then A new pet is given to majid
    And The pet is saved successfully
