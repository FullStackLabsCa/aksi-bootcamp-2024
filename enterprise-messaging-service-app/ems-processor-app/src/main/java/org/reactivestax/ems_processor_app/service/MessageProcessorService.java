package org.reactivestax.ems_processor_app.service;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@EnableJms
public class MessageProcessorService {

    @JmsListener(destination = "myDefaultQueue")
    public void onMessage(String message){
        System.out.println("message = " + message);
    }

}
