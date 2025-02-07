package org.reactivestax.canada_active_life.rest_assured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.reactivestax.canada_active_life.TestDataProvider.CartManagementTestDataProvider;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class CartManagementTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @MockitoBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        RestAssured.baseURI = "http://localhost:";
        RestAssured.port = port;
        baseUrl = "http://localhost:"+port+"/CanadaActiveLife/v1/cart";

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Message Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);
    }

    @Test
    void testAddToCart(){
        CartDTO cartDTORequest = CartManagementTestDataProvider.goodCartDTO.get();

        Response response = given()
                .log().all() // Log request details
                .contentType("application/json")
                .body(cartDTORequest)
                .header("x-security-header", "c355754d-0835-4579-aa17-4f87c3d844b8")
                .when()
                .post(baseUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();

        CartDTO cartDTOResponse = response.as(CartDTO.class);
        assertNotNull(cartDTOResponse);
        assertEquals(cartDTOResponse.getCost(), cartDTORequest.getCost());
        assertEquals(cartDTOResponse.getOfferedCourseId(), cartDTORequest.getOfferedCourseId());
        assertEquals(cartDTOResponse.getFamilyMemberLoginId(), cartDTORequest.getFamilyMemberLoginId());
    }

    @Test
    void testGetCart(){}

    @Test
    void testRemoveFromCart(){}

    @Test
    void testCheckoutCart_AllValidEnrollments(){}

    @Test
    void testPayCart_PaymentSucces(){}

}
