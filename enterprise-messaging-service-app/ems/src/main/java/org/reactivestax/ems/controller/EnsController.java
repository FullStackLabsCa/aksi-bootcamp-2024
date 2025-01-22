package org.reactivestax.ems.controller;

import jakarta.validation.Valid;
import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.EnsService;
import org.reactivestax.ems.validation.MessageGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<String> sendMessageViaSms(@RequestBody @Validated(MessageGroup.class) CustomerDTO customerDTO){
        ensService.sendMessageViaSms(customerDTO);
        return  ResponseEntity.ok("Message Sent Via SMS.");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendMessageViaCall(@RequestBody @Validated(MessageGroup.class) CustomerDTO customerDTO){
        ensService.sendMessageViaCall(customerDTO);
        return  ResponseEntity.ok("Message Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendMessageViaEmail(@RequestBody @Validated(MessageGroup.class) CustomerDTO customerDTO){
        ensService.sendMessageViaEmail(customerDTO);
        return  ResponseEntity.ok("Message Sent Via Email.");
    }

}
