package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.CourseMemberRegistrationDTO;
import org.reactivestax.canada_active_life.service.OfferedCourseRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/CanadaActiveLife/v1/courseRegistrations")
public class RegistrationController {

    @Autowired
    private OfferedCourseRegistrationService offeredCourseRegistrationService;

    @PostMapping("/enrollment")
    public ResponseEntity<String> enrollFamilyMemberInAnOfferedCourse(@RequestBody CourseMemberRegistrationDTO courseMemberRegistrationDTO, @RequestHeader("x-security-header") String memberLoginId){
        boolean isEnrolled = offeredCourseRegistrationService.enrollFamilyMemberInOfferedCourse(courseMemberRegistrationDTO, memberLoginId);
        if(isEnrolled) return ResponseEntity.ok("Family Member Enrolled in the Offered Course.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to enroll the Family Member in the desired offered course...");
    }

    @GetMapping("/enrollment")
    public ResponseEntity<List<CourseMemberRegistrationDTO>> getAllCourseEnrollmentsForMember(@RequestParam String familyMemberId){
        List<CourseMemberRegistrationDTO> enrollments = offeredCourseRegistrationService.getEnrollmentsForMember(Integer.parseInt(familyMemberId));
        if(!enrollments.isEmpty()) return ResponseEntity.ok(enrollments);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
