package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.CourseMemberRegistrationDTO;
import org.reactivestax.canada_active_life.dto.CourseMemberWaitlistDTO;
import org.reactivestax.canada_active_life.service.RegistrationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/CanadaActiveLife/v1/courseRegistrations")
public class RegistrationController {

    @Autowired
    private RegistrationManagementService registrationManagementService;

    @PostMapping("/enrollment")
    public ResponseEntity<String> enrollFamilyMemberInAnOfferedCourse(@RequestBody CourseMemberRegistrationDTO courseMemberRegistrationDTO, @RequestHeader("x-security-header") String memberLoginId){
        boolean isEnrolled = registrationManagementService.enrollFamilyMemberInOfferedCourse(courseMemberRegistrationDTO, memberLoginId);
        if(isEnrolled) return ResponseEntity.ok("Family Member Enrolled in the Offered Course.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to enroll the Family Member in the desired offered course...");
    }

    @GetMapping("/enrollment")
    public ResponseEntity<List<CourseMemberRegistrationDTO>> getAllCourseEnrollmentsForMember(@RequestParam String memberLoginId){
        List<CourseMemberRegistrationDTO> enrollments = registrationManagementService.getEnrollmentsForMember(memberLoginId);
        if(!enrollments.isEmpty()) return ResponseEntity.ok(enrollments);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    public ResponseEntity<String> withdrawFamilyMemberFromOfferedCourse(@RequestParam String offeredCourseId, @RequestParam String memberLoginId, @RequestHeader("x-security-header") String actorMemberLoginId){
        boolean isWithdrawn = registrationManagementService.withdrawFamilyMemberFromOfferedCourse(Integer.parseInt(offeredCourseId), memberLoginId, actorMemberLoginId);
        if(isWithdrawn) return ResponseEntity.ok("Family Member withdrawn from the Offered Course.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to withdraw the Family Member from the offered course...");
    }

    @GetMapping("/waitlist")
    public ResponseEntity<List<CourseMemberWaitlistDTO>> getAllCourseWaitlistForMember(@RequestParam String memberLoginId){
        List<CourseMemberWaitlistDTO> waitlist = registrationManagementService.getWaitlistForMember(memberLoginId);
        if(!waitlist.isEmpty()) return ResponseEntity.ok(waitlist);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
