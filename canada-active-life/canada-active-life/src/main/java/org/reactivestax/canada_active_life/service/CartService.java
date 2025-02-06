package org.reactivestax.canada_active_life.service;

import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    public boolean addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        /**
         * Check actor validity
         * check if offeredCourse is "open"
         * Check if there is seat available in the offered Course
         *      Yes - add to cart
         *      No - throw Exception
         */
        return false;
    }

    public List<CartDTO> getCartForActor(String actorMemberLoginId) {
        /**
         * check actor validity
         * query the table to get all the cart for the actor ID
         */
        return null;
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
