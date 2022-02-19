package com.bookit.step_definitions;

import com.bookit.pages.USPSZipCodePage;
import com.bookit.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.hc.core5.http.HttpStatus;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZipCodeSteps {

    Response response;
    USPSZipCodePage zipCodePage = new USPSZipCodePage();

    @Given("user sends GET request to {string} with {string} zipcode")
    public void user_sends_GET_request_to_with(String endpoint, String zipCode) {
        response = given().accept(ContentType.JSON)
                .and().pathParam("postal-code", zipCode)
                .when().get( endpoint);
    }

    @Then("city name should be {string} in response")
    public void city_name_should_be_in_response(String city) {
        assertThat(response.statusCode(), is(HttpStatus.SC_OK));

        response.prettyPrint();

        JsonPath json = response.jsonPath();
        String responseCityName = json.getString("places[0].'place name'");
        System.out.println("responseCityName = " + responseCityName);

        assertThat(responseCityName, equalToIgnoringCase(city));
    }

    @Then("user searches for {string} on USPS website")
    public void user_searches_for_on_USPS_website(String zipCode) {
        Driver.getDriver().get("https://tools.usps.com/zip-code-lookup.htm?citybyzipcode");
        zipCodePage.searchZipCode(zipCode);

    }

    @Then("city name should be {string} in result")
    public void city_name_should_be_in_result(String city) {
        System.out.println("City name: " + zipCodePage.cityNameElement.getText());

        assertThat(zipCodePage.cityNameElement.getText(), equalToIgnoringCase(city));

    }
}
