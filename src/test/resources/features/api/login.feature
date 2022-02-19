Feature: BookIt API login verifications

  Scenario: verify login with valid credentials
    Given user logged in BookIt api as teacher role
    And user sends GET request to "/api/users/me"
    Then status code should be 200
    And content type is "application/json"
    And role is "teacher"

  @ui
  Scenario: verify user details with ui and api
    Given user logged in BookIt api as teacher role
    And user sends GET request to "/api/users/me"
    Then status code should be 200
    And user logged in to BookIt app as teacher role
    And user is on self page
    Then user should see same info on UI and API

