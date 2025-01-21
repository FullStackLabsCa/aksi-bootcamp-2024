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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOtpViaSms(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.SMS, MessageType.OTP);
    }

    private void createCustomerAndSendIdToJms(CustomerDTO customerDTO, DeliveryMode deliveryMode, MessageType messageType) {
        Customer customer = customerRepository.findByCustomerId(customerDTO.getCustomerId());
        Message message = Message.builder()
                .deliveryMode(deliveryMode)
                .messageType(messageType)
                .messageData(generateRandomOTP())
                .customer(customer)
                .build();
        message.setCustomer(customer);

        messageRepository.save(message);

        jmsTemplate.convertAndSend("myDefaultQueue", message.getId());
    }

    private String generateRandomOTP() {
        Random random = new Random();
        int randomSixDigitNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomSixDigitNumber);
    }

    public void sendOtpViaCall(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.CALL, MessageType.OTP);
    }

    public void sendOtpViaEmail(CustomerDTO customerDTO) {
        createCustomerAndSendIdToJms(customerDTO, DeliveryMode.EMAIL, MessageType.OTP);
    }

    public boolean verifyOtp(CustomerDTO customerDTO) {
        String otpData = customerDTO.getMessage();
        String otpGenerated = getGeneratedOTP(customerDTO.getCustomerId());
        if(otpData.equals(otpGenerated)){
            updateCustomerStatusToVerified(customerDTO.getCustomerId());
            return true;
        } else return false;
    }

    private void updateCustomerStatusToVerified(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        customer.setVerificationStatus(true);
        customerRepository.save(customer);
    }

    private String getGeneratedOTP(String customerId) {
        LocalDateTime deadlineTime = LocalDateTime.now().minusMinutes(2);
        // Get the Last Posted Message of Type OTP from the Message Table for this customerID
        Optional<Message> message = messageRepository.findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterOrderByCreationTimeDesc(customerId, MessageType.OTP, deadlineTime);
        if (message.isPresent()) return message.get().getMessageData();
        else return "11111111";
    }

    public Boolean checkCustomerVerificationStatus(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        return customer.getVerificationStatus();
    }
}
