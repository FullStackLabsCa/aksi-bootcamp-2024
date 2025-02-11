package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.domain.Cart;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.CheckoutDTO;
import org.reactivestax.canada_active_life.mapper.CartMapper;
import org.reactivestax.canada_active_life.repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartManagementServiceTest {

    @Autowired
    private CartManagementService cartManagementService;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private OfferedCourseService offeredCourseService;

    @MockitoBean
    private RegistrationManagementService registrationManagementService;

    @MockitoBean
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

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
    void testAddToCart_SeatsAvailable(){
        FamilyMember familyMember = FamilyMember.builder().build();
        OfferedCourse offeredCourse = OfferedCourse.builder().build();
        Cart cart = Cart.builder()
                .cartId(1)
                .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .familyMember(FamilyMember.builder().memberLoginId("anyFamilyMember").build())
                .build();

        when(familyManagementService.checkFamilyMemberValidity(any(String.class)))
                .thenReturn(familyMember);
        when(offeredCourseService.getOfferedCourseById(any(Integer.class)))
                .thenReturn(offeredCourse);
        when(registrationManagementService.checkFamilyMemberAndOfferedCourseValidity(any(FamilyMember.class), any(OfferedCourse.class)))
                .thenReturn(true);
        when(registrationManagementService.getCostOfOfferedCourseForFamilyMember(any(OfferedCourse.class), any(FamilyMember.class)))
                .thenReturn(100.0);
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);

        CartDTO cartDTO = CartDTO.builder()
                .familyMemberLoginId("anyFamilyMember")
                .offeredCourseId(1)
                .build();
        CartDTO cartDTOResponse = cartManagementService.addOfferedCourseToCart(cartDTO, "anyActor");

        assertNotNull(cartDTOResponse);
        assertNotNull(cartDTOResponse.getId());
        verify(familyManagementService, times(2)).checkFamilyMemberValidity(any(String.class));
        verify(registrationManagementService, times(1)).checkFamilyMemberAndOfferedCourseValidity(any(FamilyMember.class), any(OfferedCourse.class));
        verify(registrationManagementService, times(1)).getCostOfOfferedCourseForFamilyMember(any(OfferedCourse.class), any(FamilyMember.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCart_ActorDNE(){}

    @Test
    void testGetCart_EmptyCart(){}

    @Test
    void testGetCart_MultipleItemsInCart(){
        FamilyMember familyMember = FamilyMember.builder().build();
        ArrayList<Cart> carts = new ArrayList<>();
        carts.add(Cart.builder()
                .cartId(1)
                .familyMember(FamilyMember.builder().memberLoginId("anyFamilyMember").build())
                .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .build());
        when(familyManagementService.checkFamilyMemberValidity(anyString()))
                .thenReturn(familyMember);
        when(cartRepository.findAllByFamilyMember_FamilyMemberId(anyInt()))
                .thenReturn(carts);

        List<CartDTO> cartDTOs = cartManagementService.getCartDTOForActor("anyFamilyMember");

        assertNotNull(cartDTOs);
        assertEquals(1, cartDTOs.size());
    }

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
    void testCheckoutCart_AllValidEnrollments(){
        FamilyMember familyMember = FamilyMember.builder().build();
        ArrayList<Cart> carts = new ArrayList<>();
        carts.add(Cart.builder()
                .cartId(1)
                .familyMember(FamilyMember.builder().memberLoginId("anyFamilyMember").build())
                .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .cost(100)
                .build());
        carts.add(Cart.builder()
                .cartId(2)
                .familyMember(FamilyMember.builder().memberLoginId("anyFamilyMember2").build())
                .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .cost(100)
                .build());
        when(familyManagementService.checkFamilyMemberValidity(anyString()))
                .thenReturn(familyMember);
        when(cartRepository.findAllByFamilyMember_FamilyMemberId(anyInt()))
                .thenReturn(carts);
        when(registrationManagementService.checkFamilyMemberAndOfferedCourseValidity(any(FamilyMember.class), any(OfferedCourse.class)))
                .thenReturn(true);

        CheckoutDTO checkoutDTO = cartManagementService.checkoutCart("anyFamilyMember");

        assertNotNull(checkoutDTO);
        assertEquals(200, checkoutDTO.getTotalCost());
        Map<Integer, Boolean> cartItemValidity = checkoutDTO.getCartItemValidity();
        assertEquals(2, cartItemValidity.size());
    }

    @Test
    void testPayCart_ActorDNE(){}

    @Test
    void testPayCart_EmptyCart(){}

    @Test
    void testPayCart_PaymentFailed(){}

    @Test
    void testPayCart_PaymentSucces(){}
}
