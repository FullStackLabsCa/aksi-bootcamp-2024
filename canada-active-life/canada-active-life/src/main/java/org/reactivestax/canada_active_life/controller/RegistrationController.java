package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseWaitlistDTO;
import org.reactivestax.canada_active_life.service.RegistrationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/CanadaActiveLife/v1/courseRegistrations")
public class RegistrationController {

    @Autowired
    private RegistrationManagementService registrationManagementService;

//    @PostMapping("/enrollment")
//    public ResponseEntity<String> enrollFamilyMemberInAnOfferedCourse(@RequestBody FamilyMemberCourseRegistrationDTO familyMemberCourseRegistrationDTO, @RequestHeader("x-security-header") String memberLoginId){
//        boolean isEnrolled = registrationManagementService.enrollFamilyMemberInOfferedCourse(familyMemberCourseRegistrationDTO, memberLoginId);
//        if(isEnrolled) return ResponseEntity.ok("Family Member Enrolled in the Offered Course.");
//        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body("Failed to enroll the Family Member in the desired offered course...");
//    }

    @GetMapping("/enrollment")
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<List<FamilyMemberCourseRegistrationDTO>> getAllCourseEnrollmentsForMember(@RequestParam String memberLoginId){
        List<FamilyMemberCourseRegistrationDTO> enrollments = registrationManagementService.getEnrollmentsForMember(memberLoginId);
        if(!enrollments.isEmpty()) return ResponseEntity.ok(enrollments);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<String> withdrawFamilyMemberFromOfferedCourse(@RequestParam String offeredCourseId, @RequestParam String memberLoginId, @RequestHeader("x-security-header") String actorMemberLoginId){
        boolean isWithdrawn = registrationManagementService.withdrawFamilyMemberFromOfferedCourse(Integer.parseInt(offeredCourseId), memberLoginId, actorMemberLoginId);
        if(isWithdrawn) return ResponseEntity.ok("Family Member withdrawn from the Offered Course.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to withdraw the Family Member from the offered course...");
    }

    @PostMapping("/waitlist")
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<String> waitlistFamilyMemberInOfferedCourse(@RequestBody FamilyMemberCourseWaitlistDTO familyMemberCourseWaitlistDTO, @RequestHeader("x-security-header") String actorMemberLoginId){
        boolean isWaitlisted = registrationManagementService.waitlistFamilyMember(actorMemberLoginId, familyMemberCourseWaitlistDTO.getFamilyMember().getMemberLoginId(), familyMemberCourseWaitlistDTO.getOfferedCourse().getOfferedCourseId());

        if(isWaitlisted) return ResponseEntity.ok("Family Member waitlisted to the Offered Course.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to Waitlist the Family Member to the offered course...");
    }

    @GetMapping("/waitlist")
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<List<FamilyMemberCourseWaitlistDTO>> getAllCourseWaitlistForMember(@RequestParam String memberLoginId){
        List<FamilyMemberCourseWaitlistDTO> waitlist = registrationManagementService.getWaitlistForMember(memberLoginId);
        if(!waitlist.isEmpty()) return ResponseEntity.ok(waitlist);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
