package org.reactivestax.ems_processor_app.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.ems_processor_app.domain.Message;
import org.reactivestax.ems_processor_app.enums.DeliveryMode;
import org.reactivestax.ems_processor_app.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@EnableJms
@Slf4j
public class MessageProcessorService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private Environment environment;

    @JmsListener(destination = "myDefaultQueue")
    public void onMessage(String message){
        Optional<Message> messageFromRepo = messageRepository.findById(Long.valueOf(message));
        if(messageFromRepo.isPresent()) {
            DeliveryMode deliveryMode = messageFromRepo.get().getDeliveryMode();
            String messageData = messageFromRepo.get().getMessageData();
            String phoneNumber;

            switch (deliveryMode){
                case SMS:
                    if(messageFromRepo.get().getPhoneNumber() != null) phoneNumber = messageFromRepo.get().getPhoneNumber();
                    else phoneNumber = messageFromRepo.get().getCustomer().getPhoneNumber();

                    log.info("Sending SMS message to " + phoneNumber + " with data " + messageData);
                    sendSmsWithTwilio(messageData, phoneNumber);
                    break;
                case CALL:
                    if(messageFromRepo.get().getPhoneNumber() != null) phoneNumber = messageFromRepo.get().getPhoneNumber();
                    else phoneNumber = messageFromRepo.get().getCustomer().getPhoneNumber();

                    log.info("Calling to " + phoneNumber + " with data " + messageData);
//                    callWithTwilio(messageData, messageFromRepo.get().getCustomer().getPhoneNumber());
                    break;
                case EMAIL:
                    String emailAddress;
                    if(messageFromRepo.get().getEmailAddress() != null) emailAddress = messageFromRepo.get().getEmailAddress();
                    else emailAddress = messageFromRepo.get().getCustomer().getEmailAddress();

                    emailWithTwilio(messageData, emailAddress);
                    break;
            }
        }
    }

    private void emailWithTwilio(String messageData, String emailAddress) {
        log.info("Sending Email to " + emailAddress + " with data " + messageData);
    }

    private void callWithTwilio(String messageData, String phoneNumber) {
        Twilio.init(Objects.requireNonNull(environment.getProperty("twilio.account.sid")), Objects.requireNonNull(environment.getProperty("twilio.auth.token")));

        String helloTwiml = new VoiceResponse.Builder()
                .say(new Say.Builder(messageData)
                        .voice(Say.Voice.POLLY_MATTHEW).build())
                .build().toXml();

        Call call = Call.creator(
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(environment.getProperty("twilio.phone.number")),
                        new Twiml(helloTwiml))
                .create();

        log.info(call.getSid());
    }

    private void sendSmsWithTwilio(String messageData, String phoneNumber) {
        Twilio.init(Objects.requireNonNull(environment.getProperty("twilio.account.sid")), Objects.requireNonNull(environment.getProperty("twilio.auth.token")));
        com.twilio.rest.api.v2010.account.Message
                .creator(new PhoneNumber(phoneNumber),
                        new PhoneNumber(environment.getProperty("twilio.phone.number")),
                        messageData)
                .create();
    }


}
