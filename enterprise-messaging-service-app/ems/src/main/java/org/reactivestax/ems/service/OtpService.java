package org.reactivestax.ems.service;

import org.reactivestax.ems.domain.Customer;
import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.enums.DeliveryMode;
import org.reactivestax.ems.enums.MessageType;
import org.reactivestax.ems.repository.CustomerRepository;
import org.reactivestax.ems.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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

    @Autowired
    private Environment environment;

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
        Optional<Message> otpGeneratedMessage = getGeneratedOTPMessage(customerDTO.getCustomerId());
        return validateOtp(customerDTO, otpGeneratedMessage, otpData);
    }

    private boolean validateOtp(CustomerDTO customerDTO, Optional<Message> otpGeneratedMessage, String otpData) {
        if(otpGeneratedMessage.isPresent()) {
            String otpGenerated = otpGeneratedMessage.get().getMessageData();
            if (otpData.equals(otpGenerated)) {
                updateCustomerStatusToVerified(customerDTO.getCustomerId());
                return true;
            } else {
                otpGeneratedMessage.get().setOtpFailureCount(otpGeneratedMessage.get().getOtpFailureCount() + 1);
                messageRepository.save(otpGeneratedMessage.get());
            }
        }
        return false;
    }

    private void updateCustomerStatusToVerified(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        customer.setVerificationStatus(true);
        customerRepository.save(customer);
    }

    private Optional<Message> getGeneratedOTPMessage(String customerId) {
        LocalDateTime deadlineTime = LocalDateTime.now().minusMinutes(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.timeout"))));
        int maxFailureCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.max-failure-count")));
        return messageRepository.findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterAndOtpFailureCountLessThanOrderByCreationTimeDesc(customerId, MessageType.OTP, deadlineTime, maxFailureCount);
    }

    public Boolean checkCustomerVerificationStatus(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        return customer.getVerificationStatus();
    }
}
