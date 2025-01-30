package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/CanadaActiveLife/v1")
public class DashboardController {

    @Autowired
    private FamilyManagementService familyManagementService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpNewMember(@RequestBody FamilyMemberDTO familyMemberDTO){

        boolean isMemberCreated = familyManagementService.signUpNewFamilyMember(familyMemberDTO);

        if(isMemberCreated) return ResponseEntity.ok("Family Member created. Please click on the Activation Link in the email to activate the account.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be created.");
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateNewMember(@RequestParam String familyMemberId, @RequestParam String uuid){

        boolean isMemberActivated = familyManagementService.activateNewSignUp(Integer.parseInt(familyMemberId), UUID.fromString(uuid));

        if(isMemberActivated) return ResponseEntity.ok("Family Member activated.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be activated.");
    }

    @PostMapping("/login")
    public ResponseEntity<UUID> loginMember(@RequestBody UserLoginDTO userLoginDTO){
        UUID uuidToken = familyManagementService.loginMember(userLoginDTO);
        return ResponseEntity.ok(uuidToken);
    }

    @PostMapping("/login/2fa")
    public ResponseEntity<String> loginVerification(@RequestBody UserLoginDTO userLoginDTO){
        boolean isVerified = familyManagementService.loginVerification(userLoginDTO);
        if(isVerified) return ResponseEntity.ok("Member Verified :-)");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Login Failed :-(");
    }

}
