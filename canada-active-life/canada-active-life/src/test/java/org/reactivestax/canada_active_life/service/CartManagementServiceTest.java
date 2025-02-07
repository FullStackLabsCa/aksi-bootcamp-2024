package org.reactivestax.canada_active_life.service;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.CartManagementTestDataProvider;
import org.reactivestax.canada_active_life.dto.CartDTO;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CartManagementServiceTest {

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
