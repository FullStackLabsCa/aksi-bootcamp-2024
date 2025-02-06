package org.reactivestax.canada_active_life.service;

import org.reactivestax.canada_active_life.dto.CartDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    public boolean addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        /**
         * Check actor validity
         * Check if there is seat available in the offered Course
         *      Yes - add to cart
         *      No - throw Exception
         * add to cart in the db
         */
        return false;
    }

    public List<CartDTO> getCartForActor(String actorMemberLoginId) {
        /**
         * check actor validity
         * query the table to get all the cart for the actor ID
         *
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
        return false;
    }
}
