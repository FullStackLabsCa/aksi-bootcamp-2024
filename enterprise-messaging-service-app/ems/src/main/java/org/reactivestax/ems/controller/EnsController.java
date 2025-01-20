package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CallDTO;
import org.reactivestax.ems.dto.EmailDTO;
import org.reactivestax.ems.dto.SmsDTO;
import org.reactivestax.ems.service.EnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ens")
public class EnsController {

    @Autowired
    private EnsService ensService;

    @PostMapping("/sms")
    public ResponseEntity<String> sendMessageViaSms(@RequestBody SmsDTO smsDTO){
        ensService.sendMessageViaSms(smsDTO);
        return  ResponseEntity.ok("Message Sent Via SMS.");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendMessageViaCall(@RequestBody CallDTO callDTO){
        ensService.sendMessageViaCall(callDTO);
        return  ResponseEntity.ok("Message Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendMessageViaEmail(@RequestBody EmailDTO emailDTO){
        ensService.sendMessageViaEmail(emailDTO);
        return  ResponseEntity.ok("Message Sent Via Email.");
    }

}
