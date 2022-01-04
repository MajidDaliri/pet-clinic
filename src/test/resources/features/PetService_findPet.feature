Feature: find pet
  Scenario: find an existing pet
    Given There is a pet with id 3
    When Someone wants to find the pet with id 3
    Then Pet with id 3 is found

  Scenario: find a missing pet
    Given There is no pet with id 241
    When Someone wants to find the pet with id 241
    Then No pet is found
