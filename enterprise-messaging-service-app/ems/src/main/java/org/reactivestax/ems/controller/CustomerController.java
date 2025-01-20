package org.reactivestax.ems.controller;

import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ens")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/customer")
    public ResponseEntity<String> createCustomer(@RequestBody Customer customer){
        customerService.saveCustomer(customer);
        return  ResponseEntity.ok("Customer Created.");
    }
}
