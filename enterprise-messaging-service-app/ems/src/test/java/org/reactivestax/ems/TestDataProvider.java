package org.reactivestax.ems;

import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.enums.DeliveryMode;
import org.reactivestax.ems.enums.MessageType;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public interface TestDataProvider {

    Supplier<String> goodCustomerJson = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test"
        }
        """;

    Supplier<String> goodCustomerJsonWithPhoneNumber = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "phoneNumber": "9055985456"
        }
        """;

    Supplier<String> goodCustomerJsonWithEmail = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "emailAddress": "Test@gmail.com"
        }
        """;

    Supplier<String> goodCustomerJsonWithPhoneNumAndEmail = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "phoneNumber": "9055985456",
          "emailAddress": "Test@gmail.com"
        }
        """;

    Supplier<String> badCustomerJsonWithNullCustomerId = () -> """
        {
            "message": "This is a message with blank CustomerId"
        }
        """;

    Supplier<String> badCustomerJsonWithBlankCustomerId = () -> """
        {
            "customerId": "  ",
            "message": "This is a message with blank CustomerId"
        }
        """;

    Supplier<String> badCustomerJsonWithInvalidCustomerId = () -> """
        {
          "customerId": "doesNotExist",
          "message": "This is my Message through SMS! Hello Test"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongPhoneNumLen = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "phoneNumber": "90559854"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongPhoneNumData = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "phoneNumber": "90559854aa"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongEmail = () -> """
        {
          "customerId": "testData",
          "message": "This is my Message through SMS! Hello Test",
          "emailAddress": "testData"
        }
        """;

    Supplier<String> goodCustomerJsonWithoutMessage = () -> """
        {
          "customerId": "testData"
        }
        """;

    Supplier<String> goodCustomerJsonWithPhoneNumberWithoutMessage = () -> """
        {
          "customerId": "testData",
          "phoneNumber": "9055985456"
        }
        """;

    Supplier<String> goodCustomerJsonWithEmailWithoutMessage = () -> """
        {
          "customerId": "testData",
          "emailAddress": "Test@gmail.com"
        }
        """;

    Supplier<String> goodCustomerJsonWithPhoneNumAndEmailWithoutMessage = () -> """
        {
          "customerId": "testData",
          "phoneNumber": "9055985456",
          "emailAddress": "Test@gmail.com"
        }
        """;

    Supplier<String> badCustomerJsonWithNullCustomerIdWithoutMessage = () -> """
        {
        }
        """;

    Supplier<String> badCustomerJsonWithBlankCustomerIdWithoutMessage = () -> """
        {
            "customerId": "  "
        }
        """;

    Supplier<String> badCustomerJsonWithInvalidCustomerIdWithoutMessage = () -> """
        {
          "customerId": "doesNotExist"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongPhoneNumLenWithoutMessage = () -> """
        {
          "customerId": "testData",
          "phoneNumber": "90559854"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongPhoneNumDataWithoutMessage = () -> """
        {
          "customerId": "testData",
          "phoneNumber": "90559854aa"
        }
        """;

    Supplier<String> badCustomerJsonWithWrongEmailWithoutMessage = () -> """
        {
          "customerId": "testData",
          "emailAddress": "testData"
        }
        """;

    Supplier<String> goodCustomerJsonForVerifyOtp = () -> """
        {
          "customerId": "testData",
          "message": "789789"
        }
        """;

    Supplier<String> badCustomerJsonCustomerDNEForVerifyOtp = () -> """
        {
          "customerId": "doesNotExist",
          "message": "789789"
        }
        """;

    Supplier<String> badCustomerJsonNullCustomerForVerifyOtp = () -> """
        {
          "message": "789789"
        }
        """;

    Supplier<String> badCustomerJsonBlankCustomerForVerifyOtp = () -> """
        {
          "customerId": "  ",
          "message": "789789"
        }
        """;

    Supplier<String> badCustomerJsonForVerifyOtpWrongSize = () -> """
        {
          "customerId": "testData",
          "message": "7897"
        }
        """;

    Supplier<String> badCustomerJsonForVerifyOtpWrongDataType = () -> """
        {
          "customerId": "testData",
          "message": "78978a"
        }
        """;

    Supplier<CustomerDTO> goodCustomerDto = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        customerDTO.setMessage("Test Message or the test DTO");
        return customerDTO;
    };

    Supplier<CustomerDTO> goodCustomerDtoWithPhoneNum = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        customerDTO.setMessage("Test Message or the test DTO");
        customerDTO.setPhoneNumber("1234567890");
        return customerDTO;
    };

    Supplier<CustomerDTO> goodCustomerDtoWithEmail = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        customerDTO.setMessage("Test Message or the test DTO");
        customerDTO.setEmailAddress("test@gmail.com");
        return customerDTO;
    };

    Supplier<CustomerDTO> goodCustomerDtoWithEmailAndPhoneNum = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        customerDTO.setMessage("Test Message or the test DTO");
        customerDTO.setPhoneNumber("1234567890");
        customerDTO.setEmailAddress("test@gmail.com");
        return customerDTO;
    };

    Supplier<Customer> goodCustomer = Customer::new;

    Supplier<CustomerDTO> goodCustomerDtoWithoutMessage = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        return customerDTO;
    };

    Supplier<CustomerDTO> goodCustomerDtoWithOTPMessage = () -> {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId("testDto");
        customerDTO.setMessage("123456");
        return customerDTO;
    };

    Supplier<Message> otpMessage = () -> Message.builder()
            .messageData("123456")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.now())
            .otpFailureCount(0)
            .build();

    Supplier<Message> wrongOtpMessage = () -> Message.builder()
            .messageData("999999")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.now())
            .otpFailureCount(0)
            .build();

    Supplier<Message> otpMessageOld = () -> Message.builder()
            .messageData("123456")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.MIN)
            .otpFailureCount(0)
            .build();

    Supplier<Message> otpMessageWithFailureCountReached = () -> Message.builder()
            .messageData("123456")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.now())
            .otpFailureCount(3)
            .build();

    Supplier<Message> otpMessageVerified = () -> Message.builder()
            .messageData("123456")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.now())
            .otpFailureCount(0)
            .verificationStatus(true)
            .verifiedTime(LocalDateTime.now())
            .build();

    Supplier<Message> otpMessageVerifiedOld = () -> Message.builder()
            .messageData("123456")
            .messageType(MessageType.OTP)
            .deliveryMode(DeliveryMode.SMS)
            .creationTime(LocalDateTime.MIN)
            .otpFailureCount(0)
            .verificationStatus(true)
            .verifiedTime(LocalDateTime.now())
            .build();
}
