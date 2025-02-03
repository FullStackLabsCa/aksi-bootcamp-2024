package org.reactivestax.ems_processor_app.repository;

import org.reactivestax.ems_processor_app.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Customer findByCustomerId(String customerId);
}
