package org.reactivestax.ems_processor_app.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestax.ems_processor_app.domain.Message;
import org.reactivestax.ems_processor_app.enums.DeliveryMode;
import org.reactivestax.ems_processor_app.enums.MessageType;
import org.reactivestax.ems_processor_app.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@EnableJms
@Slf4j
public class MessageProcessorService {

    @Autowired
    private MessageRepository messageRepository;

    @JmsListener(destination = "myDefaultQueue")
    public void onMessage(String message){
        Optional<Message> messageFromRepo = messageRepository.findById(Long.valueOf(message));
        if(messageFromRepo.isPresent()) {
            DeliveryMode deliveryMode = messageFromRepo.get().getDeliveryMode();
            MessageType messageType = messageFromRepo.get().getMessageType();
            String messageData = messageFromRepo.get().getMessageData();

            log.info("Delivery Mode:" + deliveryMode + " \n Message Type: " + messageType + " \n Message Data: " + messageData);

            /**
             * Ask Twilio to send this message data to the desired delivery mode.
             */
        }
    }

}
