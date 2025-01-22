package org.reactivestax.ems.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.reactivestax.ems.validation.MessageGroup;
import org.reactivestax.ems.validation.OtpCreationGroup;
import org.reactivestax.ems.validation.OtpVerifyGroup;

@Data
public class CustomerDTO {
    private Long id;

    @NotNull
    private String customerId;

    @NotNull(groups = {MessageGroup.class, OtpVerifyGroup.class})
    @Null(groups = {OtpCreationGroup.class})
    @Size(min = 6, max = 6, groups = {OtpVerifyGroup.class})
    @Size(min = 1, groups = {MessageGroup.class})
    private String message;
}
