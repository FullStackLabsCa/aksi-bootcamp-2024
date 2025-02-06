package org.reactivestax.canada_active_life.service;

import org.reactivestax.canada_active_life.dto.CartDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    public boolean addOfferedCourseToCart(CartDTO cartDTO, String actorLoginId) {
        return false;
    }

    public List<CartDTO> getCartForActor(String actorMemberLoginId) {
        return null;
    }

    public boolean removeItemFromCart(Integer integer, String actorLoginId) {
        return false;
    }
}
