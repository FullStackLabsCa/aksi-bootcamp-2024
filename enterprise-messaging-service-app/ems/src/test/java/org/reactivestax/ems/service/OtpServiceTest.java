package org.reactivestax.ems.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.ems.TestDataProvider;
import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.enums.MessageType;
import org.reactivestax.ems.exception.CustomerNotFoundException;
import org.reactivestax.ems.exception.MaxOTPFailureCountReachedException;
import org.reactivestax.ems.exception.MaxOTPGenerationCountReachedException;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OtpServiceTest {

    @Autowired
    private OtpService otpService;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Test
    void testSendOtpViaSms_ValidCustomer_ValidOtp(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaSms(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaSms_ValidCustomer_OtpWithFailureCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageWithFailureCountReached.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPFailureCountReachedException.class, () -> otpService.sendOtpViaSms(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaSms_ValidCustomer_OtpWithGenerationCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPGenerationCountReachedException.class, () -> otpService.sendOtpViaSms(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaSms_ValidCustomer_OtpTooOld(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageOld.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaSms(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaSms_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class, () -> otpService.sendOtpViaSms(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
        verify(messageRepository, times(0)).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaCall_ValidCustomer_ValidOtp(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaCall(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaCall_ValidCustomer_OtpWithFailureCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageWithFailureCountReached.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPFailureCountReachedException.class, () -> otpService.sendOtpViaCall(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaCall_ValidCustomer_OtpWithGenerationCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPGenerationCountReachedException.class, () -> otpService.sendOtpViaCall(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaCall_ValidCustomer_OtpTooOld(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageOld.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaCall(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaCall_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class, () -> otpService.sendOtpViaCall(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
        verify(messageRepository, times(0)).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaEmail_ValidCustomer_ValidOtp(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaEmail(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(1)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaEmail_ValidCustomer_OtpWithFailureCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageWithFailureCountReached.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPFailureCountReachedException.class, () -> otpService.sendOtpViaEmail(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaEmail_ValidCustomer_OtpTooOld(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessageOld.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertTrue(otpService.sendOtpViaEmail(customerDTO));

        verify(messageRepository, times(1)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaEmail_ValidCustomer_OtpWithGenerationCountReached(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();
        Message message = TestDataProvider.otpMessage.get();

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        messages.add(message);
        Page<Message> mockPage = new PageImpl<>(messages, PageRequest.of(0, 1), 1);

        doReturn(customer).when(customerRepository).findByCustomerId(any(String.class));
        doReturn(mockPage).when(messageRepository).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));

        assertThrows(MaxOTPGenerationCountReachedException.class, () -> otpService.sendOtpViaEmail(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

    @Test
    void testSendOtpViaEmail_InvalidCustomer(){
        CustomerDTO customerDTO = TestDataProvider.goodCustomerDtoWithoutMessage.get();
        Customer customer = TestDataProvider.goodCustomer.get();

        doReturn(null).when(customerRepository).findByCustomerId(any(String.class));

        assertThrows(CustomerNotFoundException.class, () -> otpService.sendOtpViaEmail(customerDTO));

        verify(messageRepository, times(0)).save(any(Message.class));
        verify(messageRepository, times(0)).findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(any(String.class), any(MessageType.class), any(LocalDateTime.class), any(PageRequest.class));
//        verify(jmsTemplate, times(0)).convertAndSend("myDefaultQueue", Optional.ofNullable(any()));
    }

}
