package org.reactivestax.ems_processor_app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;

    private LocalDateTime creationTime;
    private LocalDateTime updatedTime;

}
