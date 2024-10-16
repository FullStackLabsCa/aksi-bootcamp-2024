package io.reactivestax.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PositionId implements Serializable {

    private String accountNumber;
    private int securityID;

    public PositionId(){
    }

    public PositionId(String accountNumber, int securityID) {
        this.accountNumber = accountNumber;
        this.securityID = securityID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionId that = (PositionId) o;
        return securityID == that.securityID && Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, securityID);
    }
}
