package org.reactivestax.ems.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.exception.CustomerNotFoundException;
import org.reactivestax.ems.service.EnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnsController.class)
class EnsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnsService ensService;

    static Stream<Arguments> messageDeliveryOptions(){
        return Stream.of(
                Arguments.of("sms", "SMS"),
                Arguments.of("call", "Call"),
                Arguments.of("email", "Email")
        );
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessage_GoodCustomer(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/ens/" + deliveryOption;
        String expectedContent = "Message Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJson.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @Test
    void testSendMessageWithSms_GoodCustomerWithPhoneNumber() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumber.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSms_GoodCustomerWithEmail() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSms_GoodCustomerWithPhNumAndEmail() throws  Exception {
        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumAndEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Sent Via SMS."));
    }

    @Test
    void testSendMessageWithSms_BadCustomerWithoutCustomerId() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithNullCustomerId.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                .content(customerJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @Test
    void testSendMessageWithSms_BadCustomerWithBlankCustomerId() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithBlankCustomerId.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @Test
    void testSendMessageWithSms_NonExistingCustomer() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithInvalidCustomerId.get();

        doThrow(CustomerNotFoundException.class).when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer Not Found"));
    }

    @Test
    void testSendMessageWithSms_BadCustomerWithWrongPhoneNumberLength() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumLen.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid length of Phone Number"));
    }

    @Test
    void testSendMessageWithSms_BadCustomerWithWrongPhoneNumberData() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumData.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid digits in Phone Number"));
    }

    @Test
    void testSendMessageWithSms_BadCustomerWithWrongEmail() throws Exception{
        String customerJson = TestDataProvider.badCustomerJsonWithWrongEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post("/api/ens/sms")
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.emailAddress").value("Invalid Email Address"));
    }

}
