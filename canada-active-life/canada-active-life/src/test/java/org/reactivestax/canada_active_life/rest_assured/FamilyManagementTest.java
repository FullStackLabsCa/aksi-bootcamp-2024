package org.reactivestax.canada_active_life.rest_assured;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.dto.UserVerificationDTO;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class FamilyManagementTest {

    private static final String BASE_URL = "http://localhost:8080/CanadaActiveLife/v1";

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Test
    void signUpTest(){
        FamilyMemberDTO familyMemberDTO = TestDataProvider.goodFamilyMemberDTO.get();

        // Call SignUp
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(familyMemberDTO)
                .when()
                .post(BASE_URL + "/signup")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String verificationMessage = response.asString();
        assertFalse(verificationMessage.isEmpty());
        assertThat(verificationMessage).isEqualTo("Family Member created. Please click on the Activation Link in the email to activate the account.");
    }

    @Test
    void addMemberToGroupTest() {
        FamilyMemberDTO familyMemberDTO = TestDataProvider.goodFamilyMemberDTO.get();

        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(familyMemberDTO)
                .header("x-security-header", "68ef02ef-80da-43bc-b4a4-d0625b5f5685")
                .when()
                .post(BASE_URL + "/members")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String verificationMessage = response.asString();
        assertFalse(verificationMessage.isEmpty());
        assertThat(verificationMessage).isEqualTo("Family Member added to group.");

    }

    @Test
    void getMemberTest(){
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .queryParam("memberLoginId", "12122")
                .when()
                .get(BASE_URL + "/members")
                .then()
                .statusCode(200)
                .extract()
                .response();

        FamilyMemberDTO familyMemberDTO = response.as(FamilyMemberDTO.class);
        assertThat(familyMemberDTO).isNotNull();
        assertEquals("Akshat Doe", familyMemberDTO.getName());
    }

    @Test
    void activateFamilyMemberTest() {
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .queryParam("familyMemberId", "14")
                .queryParam("uuid", "68ef02ef-80da-43bc-b4a4-d0625b5f5685")
                .when()
                .get(BASE_URL + "/activate-account")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseString = response.asString();
        assertThat(responseString).isNotNull();
        assertEquals("Family Member activated.", responseString);
    }

    @Test
    void deactivateFamilyMemberTest(){
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .queryParam("memberLoginId", "121222")
                .header("x-security-header", "68ef02ef-80da-43bc-b4a4-d0625b5f5685")
                .when()
                .delete(BASE_URL + "/members")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseString = response.asString();
        assertThat(responseString).isNotNull();
        assertEquals("Family Member Deactivated", responseString);
    }

    @Test
    void updateFamilyMemberTest() {
        FamilyMemberDTO familyMemberDTO = TestDataProvider.goodFamilyMemberDTOForPatch.get();

        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(familyMemberDTO)
                .queryParam("memberLoginId", "12122")
                .when()
                .patch(BASE_URL + "/members")
                .then()
                .statusCode(200)
                .extract()
                .response();

        FamilyMemberDTO updatedFamilyMemberDTO = response.as(FamilyMemberDTO.class);
        assertThat(updatedFamilyMemberDTO).isNotNull();
        assertEquals(familyMemberDTO.getCountry(), updatedFamilyMemberDTO.getCountry());
        assertEquals(familyMemberDTO.getProvince(), updatedFamilyMemberDTO.getProvince());

    }

    @Test
    void loginTest() {
        UserLoginDTO userLoginDTO = TestDataProvider.goodLoginDTO.get();

        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(userLoginDTO)
                .when()
                .post(BASE_URL + "/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        UUID uuid = response.as(UUID.class);

        assertThat(uuid).isNotNull();
    }

    @Test
    void loginVerificationTest() {

        UserLoginDTO userLoginDTO = TestDataProvider.goodLoginDTO.get();

        Response loginResponse = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(userLoginDTO)
                .when()
                .post(BASE_URL + "/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        UUID uuid = loginResponse.as(UUID.class);

        UserVerificationDTO userVerificationDTO = TestDataProvider.goodLoginVerificationDTO.get();

        Response loginVerificationResponse = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(userVerificationDTO)
                .header("x-security-header", uuid.toString())
                .when()
                .post(BASE_URL + "/login/2fa")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseString = loginVerificationResponse.asString();
        assertThat(responseString).isNotNull();
        assertEquals("Member Verified :-)", responseString);
    }
}
