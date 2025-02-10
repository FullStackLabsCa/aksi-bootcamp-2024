package org.reactivestax.canada_active_life.TestDataProvider;

import org.reactivestax.canada_active_life.dto.CartDTO;

import java.util.function.Supplier;

public interface CartManagementTestDataProvider {

    Supplier<CartDTO> goodCartDTO = () ->
            CartDTO.builder()
                    .cost(700)
                    .offeredCourseId(4)
                    .familyMemberLoginId("c355754d-0835-4579-aa17-4f87c3d844b8")
                    .build();

    Supplier<String> goodCartDtoJSON = () ->
            "{\n" +
                    "  \"cost\": 700,\n" +
                    "  \"offeredCourseId\": 4,\n" +
                    "  \"familyMemberLoginId\": \"c355754d-0835-4579-aa17-4f87c3d844b8\"\n" +
                    "}";

    Supplier<String> paymentDTOJsonSupplier = () ->
            "{\n" +
                    "  \"cost\": 100.50,\n" +
                    "  \"cardholderName\": \"John Doe\",\n" +
                    "  \"cardNumber\": \"1234567812345678\",\n" +
                    "  \"cardExpirationDate\": \"12/25\",\n" +
                    "  \"cardCvv\": \"123\"\n" +
                    "}";
}
