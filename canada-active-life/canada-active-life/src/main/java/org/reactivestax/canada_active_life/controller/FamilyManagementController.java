package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/CanadaActiveLife/v1/members")
public class FamilyManagementController {

    @Autowired
    private FamilyManagementService familyManagementService;

    @PostMapping
    public ResponseEntity<String> addFamilyMemberToExistingGroup(@RequestBody FamilyMemberDTO familyMemberDTO, @RequestHeader("x-security-header") String actorId){

        boolean isFamilyMemberAdded = familyManagementService.addFamilyMember(familyMemberDTO, actorId);

        if(isFamilyMemberAdded) return ResponseEntity.ok("Family Member added to group.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be added to the group.");
    }

    @GetMapping
    public ResponseEntity<FamilyMemberDTO> getFamilyMemberDetails(@RequestParam String memberLoginId){
        FamilyMemberDTO familyMemberDTO = familyManagementService.getFamilyMember(memberLoginId);
        return ResponseEntity.ok(familyMemberDTO);
    }

    @PatchMapping
    public ResponseEntity<String> updateFamilyMember(@RequestBody FamilyMemberDTO familyMemberDTO){
        boolean isFamilyMemberUpdated = familyManagementService.updateFamilyMemberInfo(familyMemberDTO);

        if(isFamilyMemberUpdated) return ResponseEntity.ok("Family Member info updated.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be updated.");
    }

    @DeleteMapping
    public ResponseEntity<String> deactivateFamilyMember(@RequestParam String memberLoginId, @RequestHeader("x-security-header") String actorId){
        boolean isDeactivated = familyManagementService.deactivateFamilyMember(memberLoginId, actorId);

        if(isDeactivated) return ResponseEntity.ok("Family Member Deactivated");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be deactivated.");
    }

}
