package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.CheckoutDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.reactivestax.canada_active_life.service.CartManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/CanadaActiveLife/v1/cart")
public class CartController {

    @Autowired
    private CartManagementService cartManagementService;

    @PostMapping
    public ResponseEntity<CartDTO> addOfferedCourseToCart(@RequestBody CartDTO cartDTO, @RequestHeader("x-security-header") String actorLoginId){

        CartDTO cart = cartManagementService.addOfferedCourseToCart(cartDTO, actorLoginId);

        if(cart != null) return ResponseEntity.ok(cart);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    public ResponseEntity<List<CartDTO>> getCartForActor(@RequestHeader("x-security-header") String actorMemberLoginId){
        List<CartDTO> cartDTOList = cartManagementService.getCartDTOForActor(actorMemberLoginId);

        if(!cartDTOList.isEmpty()) return ResponseEntity.ok(cartDTOList);
        else return ResponseEntity.ok(new ArrayList<CartDTO>());
    }

    @DeleteMapping
    public ResponseEntity<String> removeInterestedCourseFromCart(@RequestParam String cartId, @RequestHeader("x-security-header") String actorLoginId){
        boolean isRemoved = cartManagementService.removeItemFromCart(Integer.valueOf(cartId), actorLoginId);

        if(isRemoved) return ResponseEntity.ok("Item removed from Cart.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Could not remove item from Cart.");
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutDTO> checkoutCart(@RequestHeader("x-security-header") String actorLoginId){
        CheckoutDTO checkoutDTO = cartManagementService.checkoutCart(actorLoginId);
        return ResponseEntity.ok(checkoutDTO);
    }

    @PostMapping("/checkout/pay")
    public ResponseEntity<String> payForCart(@RequestBody PaymentDTO paymentDTO, @RequestHeader("x-security-header") String actorLoginId){
        boolean isPaymentSuccessful = cartManagementService.payForCart(paymentDTO, actorLoginId);

        if(isPaymentSuccessful) return ResponseEntity.ok("Payment Successful. Enrollments successful");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Payment Failed, please try again.");
    }
}
