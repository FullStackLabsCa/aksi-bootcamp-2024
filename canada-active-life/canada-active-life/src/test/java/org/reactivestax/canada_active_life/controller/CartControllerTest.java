package org.reactivestax.canada_active_life.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.CartManagementTestDataProvider;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.domain.Cart;
import org.reactivestax.canada_active_life.dto.CartDTO;
import org.reactivestax.canada_active_life.dto.CheckoutDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.PaymentDTO;
import org.reactivestax.canada_active_life.service.CartManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartManagementService cartManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddOfferedCourseToCartTest_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        String cartDtoJson = CartManagementTestDataProvider.goodCartDtoJSON.get();

        when(cartManagementService.addOfferedCourseToCart(any(CartDTO.class), any(String.class)))
                .thenReturn(CartDTO.builder().build());

        mockMvc.perform(post(uriTemplate)
                        .content(cartDtoJson)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddOfferedCourseToCartTest_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        String cartDtoJson = CartManagementTestDataProvider.goodCartDtoJSON.get();

        when(cartManagementService.addOfferedCourseToCart(any(CartDTO.class), any(String.class)))
                .thenReturn(null);

        mockMvc.perform(post(uriTemplate)
                        .content(cartDtoJson)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCartForActor_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        ArrayList<CartDTO> cartDTOS = new ArrayList<>();
        cartDTOS.add(CartDTO.builder().build());
        when(cartManagementService.getCartDTOForActor(any(String.class)))
                .thenReturn(cartDTOS);

        MvcResult result = mockMvc.perform(get(uriTemplate)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CartDTO> cartDTOsResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CartDTO>>() {});
        assertFalse(cartDTOsResponse.isEmpty());
    }

    @Test
    void testGetCartForActor_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        when(cartManagementService.getCartDTOForActor(any(String.class)))
                .thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(get(uriTemplate)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CartDTO> cartDTOsResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CartDTO>>() {});
        assertTrue(cartDTOsResponse.isEmpty());
    }

    @Test
    void testRemoveOfferedCourseFromCart_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        when(cartManagementService.removeItemFromCart(any(Integer.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(delete(uriTemplate)
                        .param("cartId", "1")
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Item removed from Cart."));
    }

    @Test
    void testRemoveOfferedCourseFromCart_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart";
        when(cartManagementService.removeItemFromCart(any(Integer.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(delete(uriTemplate)
                        .param("cartId", "1")
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Could not remove item from Cart."));
    }

    @Test
    void testCheckoutCart() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart/checkout";

        when(cartManagementService.checkoutCart(any(String.class)))
                .thenReturn(CheckoutDTO.builder().build());

        mockMvc.perform(post(uriTemplate)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPayCart_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart/checkout/pay";
        String paymentDtoJson = CartManagementTestDataProvider.paymentDTOJsonSupplier.get();

        when(cartManagementService.payForCart(any(PaymentDTO.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                        .content(paymentDtoJson)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Successful. Enrollments successful"));
    }

    @Test
    void testPayCart_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/cart/checkout/pay";
        String paymentDtoJson = CartManagementTestDataProvider.paymentDTOJsonSupplier.get();

        when(cartManagementService.payForCart(any(PaymentDTO.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(post(uriTemplate)
                        .content(paymentDtoJson)
                        .header("x-security-header", "anyActor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Payment Failed, please try again."));
    }
}
