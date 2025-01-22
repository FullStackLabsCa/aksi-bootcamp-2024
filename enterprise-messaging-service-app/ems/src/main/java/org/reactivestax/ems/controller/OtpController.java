package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    /**
     * Validation:
     * with group
     * Null Message Data (Must be)
     * Only CustomerId
     * @param customerDTO
     * @return
     * TODO
     */
    @PostMapping("/sms")
    public ResponseEntity<String> sendOTPViaSms(@RequestBody CustomerDTO customerDTO){
        boolean otpSendStatus = otpService.sendOtpViaSms(customerDTO);
        if(otpSendStatus) return  ResponseEntity.ok("OTP Sent Via SMS.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to send OTP!");
    }

    @PostMapping("/call")
    public ResponseEntity<String> sendOTPViaCall(@RequestBody CustomerDTO customerDTO){
        otpService.sendOtpViaCall(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Call.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendOTPViaEmail(@RequestBody CustomerDTO customerDTO){
        otpService.sendOtpViaEmail(customerDTO);
        return  ResponseEntity.ok("OTP Sent Via Email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCustomerForOTP(@RequestBody CustomerDTO customerDTO){
        boolean isOtpValid = otpService.verifyOtp(customerDTO);
        if(isOtpValid) return ResponseEntity.ok("Customer Verified");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Verification Failed!");
    }

    @GetMapping("/{customerId}/status")
    public ResponseEntity<String> checkCustomerVerificationStatus(@PathVariable String customerId){
        Boolean isCustomerVerified = otpService.checkCustomerVerificationStatus(customerId);
        if(Boolean.TRUE.equals(isCustomerVerified)) return ResponseEntity.ok("Customer Verified");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Verification Failed!");
    }
}
