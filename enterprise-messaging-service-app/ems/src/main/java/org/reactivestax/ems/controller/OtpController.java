package org.reactivestax.ems.controller;

import jakarta.validation.Valid;
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
    public ResponseEntity<String> sendOTPViaSms(@Valid @RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaSms(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via SMS.");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendOTPViaCall(@Valid @RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaCall(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendOTPViaEmail(@Valid @RequestBody CustomerDTO customerDTO){
        otpService.sendMessageViaEmail(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Email.");
    }

}
