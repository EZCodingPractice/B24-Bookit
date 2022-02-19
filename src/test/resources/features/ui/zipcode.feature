@ui @zip
Feature: USPS zipcode look up UI and API validation

  Scenario: user should see same city for API and UI zipcode lookup
    Given user sends GET request to "http://api.zippopotam.us/us/{postal-code}" with "15235" zipcode
    Then city name should be "Pittsburgh" in response
    And user searches for "15235" on USPS website
    Then city name should be "Pittsburgh PA" in result