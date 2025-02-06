package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.*;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.reactivestax.canada_active_life.exception.OfferedCourseNotAvailableForEnrollmentException;
import org.reactivestax.canada_active_life.exception.PaymentSessionExpiredException;
import org.reactivestax.canada_active_life.exception.PaymentUnsuccessfulException;
import org.reactivestax.canada_active_life.mapper.CartMapper;
import org.reactivestax.canada_active_life.repo.CartRepository;
import org.reactivestax.canada_active_life.repo.UnconfirmedPaymentRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
    private UnconfirmedPaymentRegistrationRepository unconfirmedPaymentRegistrationRepository;

    @Autowired
    private CartMapper cartMapper;

    public CartDTO addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        /**
         * Notify user to add to waitlist (Add a waitlist End Point) - TODO
         */
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorLoginId);
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(cartDTO.getFamilyMemberLoginId());
        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(cartDTO.getOfferedCourseId());

        if(offeredCourse.getAvailableForEnrollment().equals("CLOSED"))
            throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course is not available for enrollment.");

        int seatsTaken = 0;
        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = registrationManagementService.getEnrollmentsForOfferedCourse(offeredCourse);
        if(!enrollmentsForOfferedCourse.isEmpty()) seatsTaken = enrollmentsForOfferedCourse.size();

        if(seatsTaken < offeredCourse.getSeatsAvailable()) {
            Cart cart = addToCart(actor, familyMember, offeredCourse);
            CartDTO cartDto = cartMapper.toDto(cart);
            cartDto.setId(cart.getCartId());
            cartDto.setOfferedCourseId(cart.getOfferedCourse().getOfferedCourseId());
            cartDto.setFamilyMemberLoginId(cart.getFamilyMember().getMemberLoginId());
            return cartDto;
        } else throw new OfferedCourseNotAvailableForEnrollmentException("No Seats available right now in the offered course. Would you like to Waitlist?");
    }

    private Cart addToCart(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        Cart cart = Cart.builder()
                .enrollmentActorId(actor.getFamilyMemberId())
                .familyMember(familyMember)
                .offeredCourse(offeredCourse)
                .cost(registrationManagementService.getCostOfOfferedCourse(offeredCourse, actor))
                .build();
        return cartRepository.save(cart);
    }

    public List<CartDTO> getCartDTOForActor(String actorMemberLoginId) {
        List<Cart> allCart = getCartForActor(actorMemberLoginId);
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

    private List<Cart> getCartForActor(String actorMemberLoginId){
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);
        List<Cart> allCart = cartRepository.findAllByFamilyMember_FamilyMemberId(actor.getFamilyMemberId());
        return allCart;
    }

    public boolean removeItemFromCart(Integer cartId, String actorLoginId) {
        /**
         * check actor validity
         * remove the cartId for the given actorLoginId
         */
        return false;
    }

    public Double checkoutCart(String actorLoginId){
        double totalCost = 0.0;
        List<Cart> cartForActor = getCartForActor(actorLoginId);

        // Hold the offeredCourses Available and Waitlist the courses that are full.
        for(Cart cart : cartForActor){
            if(registrationManagementService.holdPositionForFamilyMemberInOfferedCourse(cart.getEnrollmentActorId(), cart.getFamilyMember(), cart.getOfferedCourse(), cart.getCost()))
                totalCost = totalCost + cart.getCost();
        }

        return totalCost;
    }

    @Transactional
    public boolean payForCart(PaymentDTO paymentDTO, String actorMemberLoginId){
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);

        List<UnconfirmedPaymentRegistration> unconfirmedRegistrations = unconfirmedPaymentRegistrationRepository.findAllByFamilyMember_MemberLoginIdAndCreationTimeStampAfter(actorMemberLoginId, LocalDateTime.now().minusMinutes(3));
        if(unconfirmedRegistrations.isEmpty()) throw new PaymentSessionExpiredException("Payment Session Expired, please try again...");

        double totalCost = 0.0;
        for(UnconfirmedPaymentRegistration unconfirmedPaymentRegistration : unconfirmedRegistrations){
            totalCost = totalCost + unconfirmedPaymentRegistration.getCost();
        }

        // Call Stripe for Payment TODO

        boolean stripePaymentSuccess = true;
        if(stripePaymentSuccess) {
            for(UnconfirmedPaymentRegistration unconfirmedPaymentRegistration : unconfirmedRegistrations) {
                if(registrationManagementService.confirmRegistrationAfterPayment(actor, unconfirmedPaymentRegistration.getFamilyMember(), unconfirmedPaymentRegistration.getOfferedCourse()))
                    deleteItemFromCart(actor.getFamilyMemberId(), unconfirmedPaymentRegistration.getFamilyMember().getFamilyMemberId(), unconfirmedPaymentRegistration.getOfferedCourse().getOfferedCourseId());
            }
        } else throw new PaymentUnsuccessfulException("Payment was unsuccessful. Please Try again..");

        return true;
    }

    private void deleteItemFromCart(int enrollmentActorId, int familyMemberId, int offeredCourseId) {
        cartRepository.deleteAllByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndEnrollmentActorId(familyMemberId, offeredCourseId, enrollmentActorId);
    }
}
