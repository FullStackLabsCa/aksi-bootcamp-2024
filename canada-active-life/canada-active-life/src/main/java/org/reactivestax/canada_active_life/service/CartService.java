package org.reactivestax.canada_active_life.service;

import org.reactivestax.canada_active_life.domain.Cart;
import org.reactivestax.canada_active_life.domain.FamilyCourseRegistration;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.reactivestax.canada_active_life.exception.OfferedCourseNotAvailableForEnrollmentException;
import org.reactivestax.canada_active_life.mapper.CartMapper;
import org.reactivestax.canada_active_life.repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private FamilyManagementService familyManagementService;

    @Autowired
    private OfferedCourseService offeredCourseService;

    @Autowired
    private RegistrationManagementService registrationManagementService;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private CartMapper cartMapper;

    public CartDTO addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        /**
         * Check actor validity - Done
         * check if offeredCourse is "open" - Done
         * Check if there is seat available in the offered Course
         *      Yes - add to cart
         *      No - throw Exception
         */
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorLoginId);
        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(cartDTO.getOfferedCourseId());

        if(offeredCourse.getAvailableForEnrollment().equals("CLOSED"))
            throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course is not available for enrollment.");

        int seatsTaken = 0;
        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = registrationManagementService.getEnrollmentsForOfferedCourse(offeredCourse);
        if(!enrollmentsForOfferedCourse.isEmpty()) seatsTaken = enrollmentsForOfferedCourse.size();

        if(seatsTaken < offeredCourse.getSeatsAvailable()) {
            Cart cart = addToCart(actor, offeredCourse);
            CartDTO cartDto = cartMapper.toDto(cart);
            cartDto.setId(cart.getCartId());
            cartDto.setOfferedCourseId(cart.getOfferedCourse().getOfferedCourseId());
            cartDto.setFamilyMemberLoginId(cart.getFamilyMember().getMemberLoginId());
            return cartDto;
        } else throw new OfferedCourseNotAvailableForEnrollmentException("No Seats available right now in the offered course");
    }

    private Cart addToCart(FamilyMember actor, OfferedCourse offeredCourse) {
        Cart cart = Cart.builder()
                .enrollmentActorId(actor.getFamilyMemberId())
                .familyMember(actor)
                .offeredCourse(offeredCourse)
                .cost(registrationManagementService.getCostOfOfferedCourse(offeredCourse, actor))
                .build();
        return cartRepository.save(cart);
    }

    public List<CartDTO> getCartForActor(String actorMemberLoginId) {
        /**
         * query the table to get all the cart for the actor ID
         */
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);
        List<Cart> allCart = cartRepository.findAllByFamilyMember_FamilyMemberId(actor.getFamilyMemberId());
        List<CartDTO> cartDTOList = new ArrayList<>();

        for(Cart cart : allCart){
            CartDTO cartDto = CartDTO.builder()
                    .id(cart.getCartId())
                    .familyMemberLoginId(cart.getFamilyMember().getMemberLoginId())
                    .offeredCourseId(cart.getOfferedCourse().getOfferedCourseId())
                    .cost(cart.getCost())
                    .build();
            cartDTOList.add(cartDto);
        }

        return cartDTOList;
    }

    public boolean removeItemFromCart(Integer cartId, String actorLoginId) {
        /**
         * check actor validity
         * remove the cartId for the given actorLoginId
         */
        return false;
    }

    public boolean checkoutCart(String actorLoginId){
        /**
         * get cart for actor
         * for each item in cart
         *      call first half of the enroll in registration service
         *          make the change to add in the table for unconfirmedPaymentRegistrations
         */
        return false;
    }

    public boolean payForCart(PaymentDTO paymentDTO, String actorMemberLoginId){
        /**
         * validate actor
         * validate there are existing items in the unconfirmedPaymentRegistrations - because of expiration time
         *      if no - throw exception paymentSessionExpired - try again
         * for the totalAmount in the cart - prepare and send stripe a payment confirmedIntent
         * if stripe success
         *      add to enrollments - second half of the enroll member
         *      remove from the unconfirmedPaymentRegistrations
         * if stripe fails
         *      keep in unconfirmedPaymentRegistrations
         *      exit with exception saying payment failed
         */
        return false;
    }
}
