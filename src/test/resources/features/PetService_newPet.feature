Feature: new pet
  Scenario: majid wants a new pet
    Given Majid is an owner
    When Majid asks for a new pet
    Then A new pet is given to majid
