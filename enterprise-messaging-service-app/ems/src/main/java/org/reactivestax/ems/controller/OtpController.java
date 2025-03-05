package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CustomerDTO;
import org.reactivestax.ems.service.OtpService;
import org.reactivestax.ems.validation.OtpCreationGroup;
import org.reactivestax.ems.validation.OtpVerifyGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@EnableMethodSecurity
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/sms")
    @PreAuthorize("hasAuthority('SCOPE_fullstacklabs.sms')")
    public ResponseEntity<String> sendOTPViaSms(@RequestBody @Validated(OtpCreationGroup.class) CustomerDTO customerDTO){
        boolean otpSendStatus = otpService.sendOtpViaSms(customerDTO);
        if(otpSendStatus) return  ResponseEntity.ok("OTP Sent Via SMS.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to send OTP!");
    }

    @PostMapping("/call")
    @PreAuthorize("hasAuthority('SCOPE_fullstacklabs.call')")
    public ResponseEntity<String> sendOTPViaCall(@RequestBody @Validated(OtpCreationGroup.class) CustomerDTO customerDTO){
        boolean otpSendStatus = otpService.sendOtpViaCall(customerDTO);
        if(otpSendStatus) return  ResponseEntity.ok("OTP Sent Via Call.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to send OTP!");
    }

    @PostMapping("/email")
    @PreAuthorize("hasAuthority('SCOPE_fullstacklabs.email')")
    public ResponseEntity<String> sendOTPViaEmail(@RequestBody @Validated(OtpCreationGroup.class) CustomerDTO customerDTO){
        boolean otpSendStatus = otpService.sendOtpViaEmail(customerDTO);
        if(otpSendStatus) return  ResponseEntity.ok("OTP Sent Via Email.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to send OTP!");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCustomerForOTP(@RequestBody @Validated(OtpVerifyGroup.class) CustomerDTO customerDTO){
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
                .body("Customer NOT Verified!");
    }
}
