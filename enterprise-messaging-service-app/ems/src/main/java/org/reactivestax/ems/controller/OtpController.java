package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/sms")
    public ResponseEntity<String> sendOTPViaSms(@RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaSms(customerDTO);
        return  ResponseEntity.ok("Message Sent Via SMS.");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendOTPViaCall(@RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaCall(customerDTO);
        return  ResponseEntity.ok("Message Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendOTPViaEmail(@RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaEmail(customerDTO);
        return  ResponseEntity.ok("Message Sent Via Email.");
    }

}
