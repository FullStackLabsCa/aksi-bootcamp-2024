package org.reactivestax.ems.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.exception.CustomerNotFoundException;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));

        ensService.sendMessageViaSms(customerDTO);

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendMessageViaSms_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class,() -> ensService.sendMessageViaSms(customerDTO));
    }

    @Test
    void testSendMessageViaCall_ValidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));

        ensService.sendMessageViaCall(customerDTO);

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendMessageViaCall_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class,() -> ensService.sendMessageViaCall(customerDTO));
    }

    @Test
    void testSendMessageViaEmail_ValidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));

        ensService.sendMessageViaEmail(customerDTO);

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendMessageViaEmail_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDto.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class,() -> ensService.sendMessageViaEmail(customerDTO));
    }
}
