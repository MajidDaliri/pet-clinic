Feature: find owner
  Scenario: find an existing owner
    Given There is an owner with id 3
    When Someone wants to find the account with id 3
    Then Account with id 3 is found

  Scenario: find a missing owner
    Given There is no owner with id 241
    When Someone wants to find the account with id 241
    Then No owner is found
