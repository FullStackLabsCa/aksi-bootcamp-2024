package org.reactivestax.canada_active_life.rest_assured;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

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
    void testAddToCart_ActorDNE(){}

    @Test
    void testAddToCart_FamilyMemberDNE(){}

    @Test
    void testAddToCart_InactiveFamilyMember(){}

    @Test
    void testAddToCart_OfferedCourseClosed(){}

    @Test
    void testAddToCart_FamilyMemberAlreadyEnrolledInOfferedCourse(){}

    @Test
    void testAddToCart_NoSeatsAvailableInOfferedCourse(){}

    @Test
    void testAddToCart_SeatsAvailable(){}

    @Test
    void testGetCart_ActorDNE(){}

    @Test
    void testGetCart_EmptyCart(){}

    @Test
    void testGetCart_MultipleItemsInCart(){}

    @Test
    void testRemoveFromCart_ActorDNE(){}

    @Test
    void testRemoveFromCart_ItemDNE(){}

    @Test
    void testRemoveFromCart_GoodCase(){}

    @Test
    void testCheckoutCart_EmptyCart(){}

    @Test
    void testCheckoutCart_InvalidEnrollments_FamilyMemberNotActivatedException(){}

    @Test
    void testCheckoutCart_InvalidEnrollments_OfferedCourseNotAvailableForEnrollmentException(){}

    @Test
    void testCheckoutCart_InvalidEnrollments_MemberAlreadyEnrolledInOfferedCourseException(){}

    @Test
    void testCheckoutCart_PartialValidEnrollments(){}

    @Test
    void testCheckoutCart_AllValidEnrollments(){}

    @Test
    void testPayCart_ActorDNE(){}

    @Test
    void testPayCart_EmptyCart(){}

    @Test
    void testPayCart_PaymentFailed(){}

    @Test
    void testPayCart_PaymentSucces(){}

}
