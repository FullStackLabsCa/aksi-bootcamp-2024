package org.reactivestax.ems.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(unique = true)
    private String customerId;
    private String firstName;
    private String lastName;
    private Long phoneNumber;
    private String emailAddress;

    @Builder.Default
    private LocalDateTime creationTime = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedTime;

}
