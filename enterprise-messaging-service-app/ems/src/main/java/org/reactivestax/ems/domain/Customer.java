package org.reactivestax.ems.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Long phoneNumber;
    private String emailAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Message> messages = new ArrayList<>();

    @Builder.Default
    private Boolean verificationStatus = false;

    @Builder.Default
    private LocalDateTime creationTime = LocalDateTime.now();
    private LocalDateTime updatedTime;

    public void addMessageForCustomer(Message message){
        this.messages.add(message);
    }
}
