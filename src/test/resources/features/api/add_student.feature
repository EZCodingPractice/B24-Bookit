Feature: Add new student

  Scenario: add new student and verify status code 201
    Given user logged in BookIt api as teacher role
    And user sends POST request to "/api/students/student" with following information
      | first-name      | harold              |
      | last-name       | finch               |
      | email           | cucumber1@gmail.com |
      | password        | abc123              |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                   |
      | team-name       | Nukes               |
    Then status code should be 201
    And user deletes previously created student
