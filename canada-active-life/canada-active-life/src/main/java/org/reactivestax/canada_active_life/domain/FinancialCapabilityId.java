package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class FinancialCapabilityId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "facilityId")
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "capabilityId")
    private Capability capability;
}
