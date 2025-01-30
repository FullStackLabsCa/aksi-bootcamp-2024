package org.reactivestax.canada_active_life.rest_assured;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestax.canada_active_life.TestDataProvider;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

        // Family Member Assertions

        // Family Group Assertions

        // UUID Token Assertions
    }

    @Test
    void addMemberToGroupTest() {
        FamilyMemberDTO familyMemberDTO = TestDataProvider.goodFamilyMemberDTO.get();

        // Call SignUp
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
}
