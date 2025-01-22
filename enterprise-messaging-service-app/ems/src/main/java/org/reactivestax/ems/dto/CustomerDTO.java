package org.reactivestax.ems.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.reactivestax.ems.validation.MessageGroup;
import org.reactivestax.ems.validation.OtpCreationGroup;
import org.reactivestax.ems.validation.OtpVerifyGroup;

@Data
public class CustomerDTO {
    private Long id;

    @NotNull(groups = {MessageGroup.class, OtpVerifyGroup.class, OtpCreationGroup.class}, message = "Customer Id cannot be Null.")
    @NotBlank(groups = {MessageGroup.class, OtpVerifyGroup.class, OtpCreationGroup.class}, message = "Customer ID cannot be blank.")
    private String customerId;

    @Null(groups = {OtpVerifyGroup.class})
    @Length(min = 10, max = 10, groups = {MessageGroup.class, OtpCreationGroup.class}, message = "Invalid length of Phone Number")
    @Pattern(regexp = "^\\d+$", groups = {MessageGroup.class, OtpCreationGroup.class}, message = "Invalid digits in Phone Number")
    private String phoneNumber;

    @Null(groups = {OtpVerifyGroup.class})
    @Email(groups = {MessageGroup.class, OtpCreationGroup.class}, message = "Invalid Email Address")
    private String emailAddress;

    @NotNull(groups = {MessageGroup.class, OtpVerifyGroup.class}, message = "OTP cannot be Null")
    @NotBlank(groups = {MessageGroup.class, OtpVerifyGroup.class}, message = "OTP cannot be Blank")
    @Null(groups = {OtpCreationGroup.class})
    @Size(min = 6, max = 6, groups = {OtpVerifyGroup.class}, message = "Invalid length for OTP")
    @Pattern(regexp = "^\\d+$", groups = {OtpVerifyGroup.class}, message = "Invalid format for OTP, Please Use Valid Characters.")
    @Size(min = 1, groups = {MessageGroup.class}, message = "Invalid length for the Message")
    private String message;
}
