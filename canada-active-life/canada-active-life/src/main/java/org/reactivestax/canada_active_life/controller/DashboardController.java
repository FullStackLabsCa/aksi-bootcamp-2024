package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.*;
import org.reactivestax.canada_active_life.service.DashboardService;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/CanadaActiveLife/v1")
public class DashboardController {

    @Autowired
    private FamilyManagementService familyManagementService;

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signUpNewMember(@RequestBody FamilyMemberDTO familyMemberDTO){

        boolean isMemberCreated = familyManagementService.signUpNewFamilyMember(familyMemberDTO);
        if(isMemberCreated) return ResponseEntity.ok(ApiResponse.success("Family Member created. Please click on the Activation Link in the email to activate the account.", null));
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Family Member could not be created."));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<ApiResponse<String>> activateNewMember(@RequestParam String familyMemberId, @RequestParam String uuid){

        boolean isMemberActivated = familyManagementService.activateNewSignUp(Integer.parseInt(familyMemberId), UUID.fromString(uuid));

        if(isMemberActivated) return ResponseEntity.ok(ApiResponse.success("Family Member activated.", null));
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Family Member could not be activated."));
    }

    @PostMapping("/login")
    @PreAuthorize("hasAuthority('NONE')")
    public ResponseEntity<UUID> loginMember(@RequestBody UserLoginDTO userLoginDTO){
        UUID uuidToken = familyManagementService.loginMember(userLoginDTO);
        if(uuidToken != null) return ResponseEntity.ok(uuidToken);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/login/2fa")
    @PreAuthorize("hasAuthority('ROLE_UNVERIFIED')")
    public ResponseEntity<ApiResponse<LoginVerificationResponseDTO>> loginVerification(@RequestBody UserVerificationDTO userVerificationDTO){
        LoginVerificationResponseDTO verificationResponseDTO = familyManagementService.loginVerification(userVerificationDTO);
        if(verificationResponseDTO.isVerified()) return ResponseEntity.ok(ApiResponse.success("User Login Successful", verificationResponseDTO));
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Login Unsuccessful. Failed to Verify OTP."));
    }

    @PostMapping("/browse_offered_courses")
    public ResponseEntity<List<OfferedCourseDTO>> browseCourses(@RequestBody SearchCriteriaDTO searchCriteriaDTO){

        List<OfferedCourseDTO> offeredCourseDTOS = dashboardService.browseCourses(searchCriteriaDTO);
        if(!offeredCourseDTOS.isEmpty()) return ResponseEntity.ok(offeredCourseDTOS);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
