package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.*;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.CheckoutDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.reactivestax.canada_active_life.exception.*;
import org.reactivestax.canada_active_life.mapper.CartMapper;
import org.reactivestax.canada_active_life.repo.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartManagementService {

    private final FamilyManagementService familyManagementService;
    private final OfferedCourseService offeredCourseService;
    private final RegistrationManagementService registrationManagementService;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartDTO addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorLoginId);
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(cartDTO.getFamilyMemberLoginId());
        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(cartDTO.getOfferedCourseId());

        if(registrationManagementService.checkFamilyMemberAndOfferedCourseValidity(familyMember, offeredCourse)) {
            Cart cart = addToCart(actor, familyMember, offeredCourse);
            CartDTO cartDto = cartMapper.toDto(cart);
            cartDto.setId(cart.getCartId());
            cartDto.setOfferedCourseId(cart.getOfferedCourse().getOfferedCourseId());
            cartDto.setFamilyMemberLoginId(cart.getFamilyMember().getMemberLoginId());
            return cartDto;
        } else throw new OfferedCourseNotAvailableForEnrollmentException("No Seats Available in the Offered Course. Would you like to Waitlist?");
    }

    private Cart addToCart(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        Cart cart = Cart.builder()
                .enrollmentActorId(actor.getFamilyMemberId())
                .familyMember(familyMember)
                .offeredCourse(offeredCourse)
                .cost(registrationManagementService.getCostOfOfferedCourseForFamilyMember(offeredCourse, actor))
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
        return cartRepository.findAllByFamilyMember_FamilyMemberId(actor.getFamilyMemberId());
    }

    public boolean removeItemFromCart(Integer cartId, String actorLoginId) {
        familyManagementService.checkFamilyMemberValidity(actorLoginId);
        cartRepository.deleteByCartId(cartId);
        return true;
    }

    public CheckoutDTO checkoutCart(String actorLoginId){
        CheckoutDTO checkoutDTO = CheckoutDTO.builder().build();
        Map<Integer, Boolean> cartItemValidity = checkoutDTO.getCartItemValidity();
        double totalCost = 0.0;

        List<Cart> cartForActor = getCartForActor(actorLoginId);
        for(Cart cart : cartForActor){
            boolean isValidEnrollment = false;
            try{
                isValidEnrollment = registrationManagementService.checkFamilyMemberAndOfferedCourseValidity(cart.getFamilyMember(), cart.getOfferedCourse());
            } catch (FamilyMemberNotActivatedException | OfferedCourseNotAvailableForEnrollmentException | MemberAlreadyEnrolledInOfferedCourseException e){
                log.info(e.getMessage());
            }

            cartItemValidity.put(cart.getCartId(), isValidEnrollment);
            checkoutDTO.setCartItemValidity(cartItemValidity);

            if(isValidEnrollment) totalCost = totalCost + cart.getCost();
        }

        checkoutDTO.setTotalCost(totalCost);
        return checkoutDTO;
    }

    @Transactional
    public boolean payForCart(PaymentDTO paymentDTO, String actorMemberLoginId){
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);

        List<Cart> cartForActor = getCartForActor(actorMemberLoginId);
        if(cartForActor.isEmpty()) throw new EmptyCartException("Trying to checkout an empty cart...");

        double totalCost = 0.0;
        for(Cart cart : cartForActor){
            totalCost = totalCost + cart.getCost();
        }

        paymentDTO.setCost(totalCost);
        boolean stripePaymentSuccess = payUsingStripe(paymentDTO);

        if(stripePaymentSuccess) {
            for(Cart cart : cartForActor) {
                boolean isEnrollmentSuccessful = false;
                try{
                    isEnrollmentSuccessful = registrationManagementService.enrollFamilyMemberInOfferedCourse(actor, cart.getFamilyMember(), cart.getOfferedCourse());
                } catch (FamilyMemberWaitlistedForOfferedCourse | OfferedCourseNotAvailableForEnrollmentException e){
                    log.info(e.getMessage());
                }

                if(isEnrollmentSuccessful)
                    deleteItemFromCart(actor.getFamilyMemberId(), cart.getFamilyMember().getFamilyMemberId(), cart.getOfferedCourse().getOfferedCourseId());
            }
        } else throw new PaymentUnsuccessfulException("Payment was unsuccessful. Please Try again..");

        return true;
    }

    private void deleteItemFromCart(int enrollmentActorId, int familyMemberId, int offeredCourseId) {
        cartRepository.deleteAllByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndEnrollmentActorId(familyMemberId, offeredCourseId, enrollmentActorId);
    }

    private boolean payUsingStripe(PaymentDTO paymentDTO) {
        // TODO
        return true;
    }
}
