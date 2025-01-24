package org.reactivestax.ems.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class EnsServiceTest {

    @Autowired
    private EnsService ensService;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Test
    void testSendMessageViaSms_ValidCustomer(){
        /**
         * Create a CustomerDTO
         * Create a Customer that is Expected to be returned back by the Repo
         * Mock the customerRepo to return the created Customer
         * Mock the messageRepository
         * Mock jmsTemplate
         * Verify some logs
         */
    }

    @Test
    void testSendMessageViaSms_InvalidCustomer(){
        /**
         * Create a CustomerDTO
         * mock the customerRepo to return null
         * handle the exception
         */
    }
}
