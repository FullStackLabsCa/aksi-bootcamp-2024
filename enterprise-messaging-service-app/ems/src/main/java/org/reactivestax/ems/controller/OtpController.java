package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> sendOTPViaSms(@RequestBody CustomerDTO customerDTO){ // Use Validated with grouping to validated for OTP TODO
        otpService.sendMessageViaSms(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via SMS.");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendOTPViaCall(@RequestBody CustomerDTO customerDTO){ // Use Validated with grouping to validated for OTP TODO
        otpService.sendMessageViaCall(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendOTPViaEmail(@RequestBody CustomerDTO customerDTO){ // Use Validated with grouping to validated for OTP TODO
        otpService.sendMessageViaEmail(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCustomerForOTP(@RequestBody CustomerDTO customerDTO){ // Use Validated with grouping to validated for OTP TODO
        boolean isOtpValid = otpService.verifyOtp(customerDTO);
        if(isOtpValid) return ResponseEntity.ok("Customer Verified");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Verification Failed!");
    }
}
