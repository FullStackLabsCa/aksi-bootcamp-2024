package org.reactivestax.ems.dto;

import lombok.Data;

@Data
public class CallDTO {
    private Long id;
    private Long phoneNumber;
    private String message;
}
