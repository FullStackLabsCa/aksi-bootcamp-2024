package org.reactivestax.ems.dto;

import lombok.Data;

@Data
public class EmailDTO {
    private Long id;
    private String emailId;
    private String message;
}
