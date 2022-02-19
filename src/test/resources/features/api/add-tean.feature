@db @addTeam

Feature: add new team API and DB validation

  Scenario: Post new team and verify in database
    Given user logged in BookIt api as teacher role
    When user sends POST request to "/api/teams/team" with following information
      | campus-location | VA           |
      | batch-number    | 20           |
      | team-name       | Cucumber909 |
    Then status code should be 201
    And database should persist same team info
    And user deletes previously created team
