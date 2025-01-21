package org.reactivestax.ems.service;

import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.enums.DeliveryMode;
import org.reactivestax.ems.enums.MessageType;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessageViaSms(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.SMS, MessageType.OTP);
    }

    private void createCustomerAndSendIdToJms(CustomerDTO customerDTO, DeliveryMode deliveryMode, MessageType messageType) {
        Customer customer = customerRepository.findByCustomerId(customerDTO.getCustomerId());
        Message message = Message.builder()
                .deliveryMode(deliveryMode)
                .messageType(messageType)
                .messageData(customerDTO.getMessage())
                .customer(customer)
                .build();
        message.setCustomer(customer);

        messageRepository.save(message);

        jmsTemplate.convertAndSend("myDefaultQueue", message.getId());
    }

    public void sendMessageViaCall(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.CALL, MessageType.OTP);
    }

    public void sendMessageViaEmail(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.EMAIL, MessageType.OTP);
    }

    public boolean verifyOtp(CustomerDTO customerDTO) {
        String otpData = customerDTO.getMessage();
        String otpGenerated = getGeneratedOTP();
        if(otpData.equals(otpGenerated)){
            updateCustomerStatusToVerified(customerDTO);
            return true;
        } else return false;
    }

    private void updateCustomerStatusToVerified(CustomerDTO customerDTO) {
        Customer customer = customerRepository.findByCustomerId(customerDTO.getCustomerId());
        customer.setVerificationStatus(true);
        customerRepository.save(customer);
    }

    private String getGeneratedOTP() {
        // Get the Last Posted Message of Type OTP from the Message Table for this customerID TODO
        return "null";
    }

    public Boolean checkCustomerVerificationStatus(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        return customer.getVerificationStatus();
    }
}
