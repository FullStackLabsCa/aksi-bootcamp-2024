package org.reactivestax.ems.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OtpController.class)
class OtpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OtpService otpService;

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
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "Message Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJson.get();

        when(otpService.sendOtpViaSms(any(CustomerDTO.class)).thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }
/*
    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessage_GoodCustomerWithPhoneNumber(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "Message Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumber.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessage_GoodCustomerWithEmail(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "Message Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_GoodCustomerWithPhNumAndEmail(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "Message Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumAndEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_BadCustomerWithoutCustomerId(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithNullCustomerId.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_BadCustomerWithBlankCustomerId(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithBlankCustomerId.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_NonExistingCustomer(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithInvalidCustomerId.get();

        doThrow(CustomerNotFoundException.class).when(ensService).sendMessageViaSms(any(CustomerDTO.class));
        doThrow(CustomerNotFoundException.class).when(ensService).sendMessageViaCall(any(CustomerDTO.class));
        doThrow(CustomerNotFoundException.class).when(ensService).sendMessageViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer Not Found"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_BadCustomerWithWrongPhoneNumberLength(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumLen.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid length of Phone Number"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_BadCustomerWithWrongPhoneNumberData(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumData.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid digits in Phone Number"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendMessageWithSms_BadCustomerWithWrongEmail(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongEmail.get();

        doNothing().when(ensService).sendMessageViaSms(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.emailAddress").value("Invalid Email Address"));
    }
*/
}
