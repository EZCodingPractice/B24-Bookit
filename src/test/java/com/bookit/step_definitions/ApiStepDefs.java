package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static com.bookit.step_definitions.UIStepDefs.availableRooms;
import static com.bookit.utilities.Environment.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {

    String accessToken;
    Response response;
    Map<String, String> newRecordMap;
    List<String> apiAvailableRooms;

    @Given("user logged in BookIt api as teacher role")
    public void user_logged_in_BookIt_api_as_teacher_role() {
        accessToken = BookItApiUtil
                .getAccessToken(Environment.TEACHER_EMAIL, Environment.TEACHER_PASSWORD);
        System.out.println("Teacher email: " + Environment.TEACHER_EMAIL);
        System.out.println("Teacher password: " + Environment.TEACHER_PASSWORD);
    }

    @Given("user sends GET request to {string}")
    public void user_sends_GET_request_to(String path) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .when().get(Environment.BASE_URL + path);
        System.out.println("API endpoint: " + Environment.BASE_URL + path);
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expStatusCode) {
        assertThat(response.statusCode(), equalTo(expStatusCode));

    }

    @Then("content type is {string}")
    public void content_type_is(String expContentType) {
        assertThat(response.contentType(), equalTo(expContentType));
    }

    @And("role is {string}")
    public void roleIs(String expRole) {
        JsonPath json = response.jsonPath();
        System.out.println("role: " + json.getString("role"));
        assertThat(json.getString("role"), is(expRole));
    }

    @Then("user should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {
        // read values into a map from api
        Map<String, Object> apiUserMap = response.body().as(Map.class);

        // read values from ui using POM
        SelfPage selfPage = new SelfPage();
        String uiFullName = selfPage.fullName.getText();
        String uiRole = selfPage.role.getText();

        String apiFullName = apiUserMap.get("firstName") + " " + apiUserMap.get("lastName");

        assertThat(uiFullName, equalTo(apiFullName));
        assertThat(uiRole, equalTo(apiUserMap.get("role")));
    }

    @And("user sends POST request to {string} with following information")
    public void userSendsPOSTRequestToWithFollowingInformation(String endPoint,
                                                               Map<String, String> newEntryInfo) {
        newRecordMap = newEntryInfo;
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .and().queryParams(newEntryInfo).log().all()
                .when().post(Environment.BASE_URL + endPoint);

        response.prettyPrint();
    }

    @And("user deletes previously created student")
    public void userDeletesPreviouslyCreatedStudent() {
        int studentId = response.path("entryiId");

        System.out.println(studentId);
        given().accept(ContentType.JSON).log().all()
                .and().header("Authorization", accessToken)
                .when().delete(Environment.BASE_URL + "/api/students/" + studentId)
                .then().assertThat().statusCode(204);

    }

    @And("user sends GET request to {string} with {string}")
    public void userSendsGETRequestToWith(String endPoint, String teamId) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .and().pathParam("id", teamId).log().all()
                .when().get(Environment.BASE_URL + endPoint);
    }

    @And("team name should be {string} in response")
    public void teamNameShouldBeInResponse(String teamName) {
        response.prettyPrint();
        assertThat(response.path("name"), equalTo(teamName));

    }

    @And("database query should have same {string} and {string}")
    public void databaseQueryShouldHaveSameAnd(String teamId, String teamName) {
        String sql = "SELECT id, name FROM team WHERE id = " + teamId;

        Map<String, Object> dbTeamInfo = DBUtils.getRowMap(sql);

        System.out.println(dbTeamInfo);

        assertThat(dbTeamInfo.get("id"), equalTo(Long.parseLong(teamId)));
        assertThat(dbTeamInfo.get("name"), equalTo(teamName));
    }

    @And("database should persist same team info")
    public void databaseShouldPersistSameTeamInfo() {
        int newTeamID = response.path("entryiId");
        String sql = "SELECT * FROM team WHERE id = " + newTeamID;
        Map<String, Object> dbNewTeamMap = DBUtils.getRowMap(sql);

        System.out.println(sql);
        System.out.println(dbNewTeamMap);

        assertThat(dbNewTeamMap.get("id"), equalTo((long) newTeamID));
        assertThat(dbNewTeamMap.get("name"), equalTo(newRecordMap.get("team-name")));
        assertThat(dbNewTeamMap.get("batch_number").toString(),
                equalTo(newRecordMap.get("batch-number")));
    }

    @And("user deletes previously created team")
    public void userDeletesPreviouslyCreatedTeam() {
//        for(int i = 11699; i < 11993; i++) {
//            given().header("Authorization", accessToken)
//                    .when().delete(Environment.BASE_URL + "/api/teams/" + i)
//                    .then().log().all();
//        }
        int teamId = response.path("entryiId");
        given().accept(ContentType.JSON).log().all()
                .and().header("Authorization", accessToken)
                .when().delete(Environment.BASE_URL + "/api/teams/" + teamId)
                .then().assertThat().statusCode(200);
    }

    @And("delete user {string}")
    public void deleteUser(int userId) {
        given().header("Authorization", accessToken)
                .when().delete(Environment.BASE_URL + "/api/teams/" + userId)
                .then().log().all();
    }


    @And("user logged in to BookIt api as team lead role")
    public void userLoggedInToBookItApiAsTeamLeadRole() {
        accessToken = BookItApiUtil.getAccessToken(Environment.LEADER_EMAIL, Environment.LEADER_PASSWORD);
        System.out.println("Team lead email: " + Environment.LEADER_EMAIL);
        System.out.println("Team lead password: " + Environment.LEADER_PASSWORD);
    }

    @And("user sends GET request to {string} with:")
    public void userSendsGETRequestToWith(String endpoint, Map<String, String> queryParams) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .and().queryParams(queryParams)
                .when().get(Environment.BASE_URL + endpoint);
    }

    @And("available rooms in response should match UI results")
    public void availableRoomsInResponseShouldMatchUIResults() {
        response.prettyPrint();

        JsonPath json = response.jsonPath();
        apiAvailableRooms = json.getList("name");
        System.out.println("rooms: " + apiAvailableRooms);
        System.out.println("UI rooms: " + availableRooms);

        // assertThat(availableRooms, equalTo(availableRooms));

        assertThat(availableRooms, equalTo(response.jsonPath().getList("name")));

    }

    @And("available rooms in database should match UI and API results")
    public void availableRoomsInDatabaseShouldMatchUIAndAPIResults() {
        String query = "SELECT room.name FROM room INNER JOIN " +
                "cluster ON room.cluster_id = cluster.id WHERE cluster.name = 'light-side'";
//        List<Object> dbAvailableRooms = DBUtils.getColumnData(query, "name");
//        System.out.println(dbAvailableRooms);

        List<String> dbAvailableRooms = new ArrayList<>();
        DBUtils.getColumnData(query, "name").forEach(name -> dbAvailableRooms.add(name.toString()));
        System.out.println("dbAvailableRooms = " + dbAvailableRooms);

        // available rooms in database should match UI and API results
        assertThat(dbAvailableRooms, allOf(equalTo(apiAvailableRooms), equalTo(availableRooms)));


    }


}
