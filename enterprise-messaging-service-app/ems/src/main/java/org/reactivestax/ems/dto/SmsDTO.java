package org.reactivestax.ems.dto;

import lombok.Data;

@Data
public class SmsDTO {
    private Long id;
    private Long phoneNumber;
    private String message;
}
