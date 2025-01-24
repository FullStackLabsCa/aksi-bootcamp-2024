package org.reactivestax.ems.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.exception.CustomerNotFoundException;
import org.reactivestax.ems.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
    void testSendOTP_GoodCustomer(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "OTP Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithoutMessage.get();

        doReturn(true).when(otpService).sendOtpViaSms(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaCall(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_GoodCustomerWithPhoneNumber(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "OTP Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumberWithoutMessage.get();

        doReturn(true).when(otpService).sendOtpViaSms(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaCall(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_GoodCustomerWithEmail(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "OTP Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithEmailWithoutMessage.get();

        doReturn(true).when(otpService).sendOtpViaSms(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaCall(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_GoodCustomerWithPhNumAndEmail(String deliveryOption, String messageExpected) throws  Exception {
        String uriTemplate = "/api/otp/" + deliveryOption;
        String expectedContent = "OTP Sent Via " + messageExpected + ".";

        String customerJson = TestDataProvider.goodCustomerJsonWithPhoneNumAndEmailWithoutMessage.get();

        doReturn(true).when(otpService).sendOtpViaSms(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaCall(any(CustomerDTO.class));
        doReturn(true).when(otpService).sendOtpViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_BadCustomerWithoutCustomerId(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithNullCustomerIdWithoutMessage.get();

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_BadCustomerWithBlankCustomerId(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithBlankCustomerIdWithoutMessage.get();

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id cannot be blank."));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_NonExistingCustomer(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithInvalidCustomerIdWithoutMessage.get();

        doThrow(CustomerNotFoundException.class).when(otpService).sendOtpViaSms(any(CustomerDTO.class));
        doThrow(CustomerNotFoundException.class).when(otpService).sendOtpViaCall(any(CustomerDTO.class));
        doThrow(CustomerNotFoundException.class).when(otpService).sendOtpViaEmail(any(CustomerDTO.class));

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer Not Found"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_BadCustomerWithWrongPhoneNumberLength(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumLenWithoutMessage.get();

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid length of Phone Number"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_BadCustomerWithWrongPhoneNumberData(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongPhoneNumDataWithoutMessage.get();

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("Invalid digits in Phone Number"));
    }

    @ParameterizedTest
    @MethodSource("messageDeliveryOptions")
    void testSendOTP_BadCustomerWithWrongEmail(String deliveryOption) throws Exception{
        String uriTemplate = "/api/otp/" + deliveryOption;

        String customerJson = TestDataProvider.badCustomerJsonWithWrongEmailWithoutMessage.get();

        mockMvc.perform(post(uriTemplate)
                        .content(customerJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.emailAddress").value("Invalid Email Address"));
    }

}
