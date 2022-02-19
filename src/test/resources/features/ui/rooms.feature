@db @ui @rooms
Feature: verify room reservation functionality

  Scenario: Team lead should be able to see the available rooms
    Given user logged in to BookIt app as team lead role
    When user goes to room hunt page
    And user searches for room with date:
      | date | February 21, 2022 |
      | from | 7:00am            |
      | to   | 7:30am            |
    Then user should see available rooms
    And user logged in to BookIt api as team lead role
    And user sends GET request to "/api/rooms/available" with:
      | year            | 2022       |
      | month           | 2          |
      | day             | 21         |
      | conference-type | SOLID      |
      | cluster-name    | light-side |
      | timeline-id     | 11237      |
    Then status code should be 200
    And available rooms in response should match UI results
    And available rooms in database should match UI and API results