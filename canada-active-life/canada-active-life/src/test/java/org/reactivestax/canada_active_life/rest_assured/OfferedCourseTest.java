package org.reactivestax.canada_active_life.rest_assured;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.OfferedCourseTestDataProvider;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Slf4j
class OfferedCourseTest {

    private static final String BASE_URL = "http://localhost:8080/CanadaActiveLife/v1/offeredCourses";

    @Test
    void createOfferedCourseTest(){
        OfferedCourseDTO offeredCourseDTO = OfferedCourseTestDataProvider.goodOfferedCourseDTO.get();

        // Call SignUp
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(offeredCourseDTO)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String verificationMessage = response.asString();
        assertFalse(verificationMessage.isEmpty());
        assertThat(verificationMessage).isEqualTo("Offered Course created.");
    }

    @Test
    void getOfferedCourseTest(){
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .queryParam("offeredCourseId", "16")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        OfferedCourseDTO offeredCourseDTO = response.as(OfferedCourseDTO.class);
        assertThat(offeredCourseDTO).isNotNull();
        assertEquals(10, offeredCourseDTO.getNumOfClassesOffered());
        assertEquals(25, offeredCourseDTO.getSeatsAvailable());
        assertFalse(offeredCourseDTO.isAllDayCourse());
    }

    @Test
    void cancelOfferedCourseTest(){
        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .queryParam("offeredCourseId", "16")
                .when()
                .delete(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseString = response.asString();
        assertThat(responseString).isNotNull();
        assertEquals("OfferedCourse no more available!", responseString);
    }

    @Test
    void updateOfferedCourseTest() {
        /* TODO
        FamilyMemberDTO familyMemberDTO = FamilyManagementTestDataProvider.goodFamilyMemberDTOForPatch.get();

        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(familyMemberDTO)
                .queryParam("memberLoginId", "12122")
                .when()
                .patch(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        FamilyMemberDTO updatedFamilyMemberDTO = response.as(FamilyMemberDTO.class);
        assertThat(updatedFamilyMemberDTO).isNotNull();
        assertEquals(familyMemberDTO.getCountry(), updatedFamilyMemberDTO.getCountry());
        assertEquals(familyMemberDTO.getProvince(), updatedFamilyMemberDTO.getProvince());

         */

    }
}
