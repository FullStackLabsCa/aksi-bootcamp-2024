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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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

    public boolean sendOtpViaSms(CustomerDTO customerDTO) {
        if(!isOtpGenerationBlocked(customerDTO)) {
            fetchCustomerAndSendMsgIdToJms(customerDTO, DeliveryMode.SMS);
            return true;
        }
        return false;
    }

    public boolean sendOtpViaCall(CustomerDTO customerDTO) {
        if(!isOtpGenerationBlocked(customerDTO)) {
            fetchCustomerAndSendMsgIdToJms(customerDTO, DeliveryMode.CALL);
            return true;
        }
        return false;
    }

    public boolean sendOtpViaEmail(CustomerDTO customerDTO) {
        if(!isOtpGenerationBlocked(customerDTO)) {
            fetchCustomerAndSendMsgIdToJms(customerDTO, DeliveryMode.EMAIL);
            return true;
        }
        return false;
    }

    private boolean isOtpGenerationBlocked(CustomerDTO customerDTO){
        int timeoutForFailure = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.failure.block-timeout-hr")));
        int timeoutForGeneration = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.generation.block-timeout-hr")));
        int maxFailureCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.failure.max-count")));
        int maxGenerationCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.generation.max-count")));

        int fetchTime = Math.max(timeoutForFailure, timeoutForGeneration);
        LocalDateTime deadlineTimeForFailure = LocalDateTime.now().minusHours(fetchTime);

        Page<Message> otpMessageForFailureCheckPage = messageRepository.findByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfter(customerDTO.getCustomerId(), MessageType.OTP, deadlineTimeForFailure, PageRequest.of(0,maxGenerationCount, Sort.by(Sort.Direction.DESC, "creationTime")));
        List<Message> otpMessageForFailureCheck = otpMessageForFailureCheckPage.getContent();

        if(otpMessageForFailureCheck.isEmpty()) return false;
        Message mostRecentMessage = otpMessageForFailureCheck.get(0);

        if(Duration.between(mostRecentMessage.getCreationTime(), LocalDateTime.now()).toHours() < timeoutForFailure && mostRecentMessage.getOtpFailureCount() >= maxFailureCount) return true;

        return otpMessageForFailureCheck.size() >= maxGenerationCount;
    }

    private void fetchCustomerAndSendMsgIdToJms(CustomerDTO customerDTO, DeliveryMode deliveryMode) {
        Customer customer = customerRepository.findByCustomerId(customerDTO.getCustomerId());
        Message message = Message.builder()
                .deliveryMode(deliveryMode)
                .messageType(MessageType.OTP)
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

    public boolean verifyOtp(CustomerDTO customerDTO) {
        String userEnteredOtp = customerDTO.getMessage();
        Optional<Message> otpGeneratedMessage = getGeneratedOTPMessage(customerDTO.getCustomerId());
        if(otpGeneratedMessage.isPresent()) return validateOtp(otpGeneratedMessage.get(), userEnteredOtp);
        else return false;
    }

    private Optional<Message> getGeneratedOTPMessage(String customerId) {
        LocalDateTime deadlineTime = LocalDateTime.now().minusMinutes(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.timeout-min"))));
        int maxFailureCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.failure.max-count")));
        return messageRepository.findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterAndOtpFailureCountLessThanOrderByCreationTimeDesc(customerId, MessageType.OTP, deadlineTime, maxFailureCount);
    }

    private boolean validateOtp(Message otpGeneratedMessage, String userEnteredOtp) {
        String otpGenerated = otpGeneratedMessage.getMessageData();
        if (userEnteredOtp.equals(otpGenerated)) {
            otpGeneratedMessage.setVerificationStatus(true);
            otpGeneratedMessage.setVerifiedTime(LocalDateTime.now());
            messageRepository.save(otpGeneratedMessage);
            return true;
        } else {
            otpGeneratedMessage.setOtpFailureCount(otpGeneratedMessage.getOtpFailureCount() + 1);
            messageRepository.save(otpGeneratedMessage);
            return false;
        }
    }

    public Boolean checkCustomerVerificationStatus(String customerId) {
        LocalDateTime verificationValidTime = LocalDateTime.now().minusMinutes(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.application.otp.verification-timeout-min"))));
        Optional<Message> otpMessage = messageRepository.findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterOrderByCreationTimeDesc(customerId, MessageType.OTP, verificationValidTime);
        return otpMessage.map(Message::isVerificationStatus).orElse(false);
    }
}
