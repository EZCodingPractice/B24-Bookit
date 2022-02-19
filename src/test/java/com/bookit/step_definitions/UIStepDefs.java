package com.bookit.step_definitions;

import com.bookit.pages.HomePage;
import com.bookit.pages.HuntPage;
import com.bookit.pages.LoginPage;
import com.bookit.pages.SpotsPage;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.Driver;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIStepDefs {

    HomePage homePage = new HomePage();
    HuntPage huntPage = new HuntPage();
    SpotsPage spotsPage = new SpotsPage();
    static List<String> availableRooms;

    @Given("user logged in to BookIt app as teacher role")
    public void user_logged_in_to_BookIt_app_as_teacher_role() {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.logIn(Environment.TEACHER_EMAIL, Environment.TEACHER_PASSWORD);
    }

    @Given("user logged in to BookIt app as team lead role")
    public void userLoggedInToBookItAppAsTeamLeadRole() {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.logIn(Environment.LEADER_EMAIL, Environment.LEADER_PASSWORD);
        
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), 6);
        wait.until(ExpectedConditions.urlContains("map"));
        assertTrue(Driver.getDriver().getCurrentUrl().endsWith("map"));
    }
     

    @Given("user is on self page")
    public void user_is_on_self_page() {
        homePage.gotoSelf();
    }


    @When("user goes to room hunt page")
    public void user_goes_to_room_hunt_page() {
        homePage.hunt.click();
    }

    @And("user searches for room with date:")
    public void userSearchesForRoomWithDate(Map<String, String> dateInfo) {
        huntPage.dateField.sendKeys(dateInfo.get("date"));
        huntPage.selectStartTime(dateInfo.get("from"));
        huntPage.selectFinishTime(dateInfo.get("to"));
        huntPage.submitBtn.click();
    }

    @Then("user should see available rooms")
    public void userShouldSeeAvailableRooms() {
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),10);
        wait.until(ExpectedConditions.visibilityOfAllElements(spotsPage.roomNames));

        availableRooms = BrowserUtils.getElementsText(spotsPage.roomNames);

        System.out.println("availableRooms: " + availableRooms);

        assertEquals(7,availableRooms.size());
    }


}
