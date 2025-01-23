package org.reactivestax.ems.service;

import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.enums.DeliveryMode;
import org.reactivestax.ems.enums.MessageType;
import org.reactivestax.ems.exception.CustomerNotFoundException;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class EnsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessageViaSms(CustomerDTO customerDTO) {
        findCustomerAndSaveToDbAndSendMsgToJms(customerDTO, DeliveryMode.SMS);
    }

    private void findCustomerAndSaveToDbAndSendMsgToJms(CustomerDTO customerDTO, DeliveryMode deliveryMode) {
        Customer customer = customerRepository.findByCustomerId(customerDTO.getCustomerId());

        Message message = Message.builder()
                .deliveryMode(deliveryMode)
                .messageType(MessageType.MESSAGE)
                .phoneNumber(customerDTO.getPhoneNumber())
                .emailAddress(customerDTO.getEmailAddress())
                .messageData(customerDTO.getMessage())
                .customer(customer)
                .build();

        messageRepository.save(message);
        jmsTemplate.convertAndSend("myDefaultQueue", message.getId());
    }

    public void sendMessageViaCall(CustomerDTO customerDTO) {
        findCustomerAndSaveToDbAndSendMsgToJms(customerDTO, DeliveryMode.CALL);
    }

    public void sendMessageViaEmail(CustomerDTO customerDTO) {
        findCustomerAndSaveToDbAndSendMsgToJms(customerDTO, DeliveryMode.EMAIL);
    }
}
