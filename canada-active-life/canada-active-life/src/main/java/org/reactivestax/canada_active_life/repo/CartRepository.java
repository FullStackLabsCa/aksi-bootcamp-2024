package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart> {
    List<Cart> findAllByFamilyMember_FamilyMemberId(int familyMemberId);
}
