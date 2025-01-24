package org.reactivestax.ems;

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
}
