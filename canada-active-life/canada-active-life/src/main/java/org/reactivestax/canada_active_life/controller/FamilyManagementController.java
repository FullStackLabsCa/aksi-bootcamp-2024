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

        boolean isFamilyMemberAdded = familyManagementService.addFamilyMember(familyMemberDTO, Integer.valueOf(actorId));

        if(isFamilyMemberAdded) return ResponseEntity.ok("Family Member added to group.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be added to the group.");
    }

    @GetMapping
    public ResponseEntity<FamilyMemberDTO> getFamilyMemberDetails(@RequestParam int familyMemberId){
        Optional<FamilyMemberDTO> familyMemberDTO = familyManagementService.getFamilyMember(familyMemberId);
        return familyMemberDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping
    public ResponseEntity<String> updateFamilyMember(@RequestBody FamilyMemberDTO familyMemberDTO){
        /**
         *
         */
        return ResponseEntity.ok("Family Member info updated.");
    }

    @GetMapping
    public ResponseEntity<String> deactivateFamilyMember(@RequestParam int familyMemberId, @RequestHeader("x-security-header") String actorId){
        /**
         *
         */
        return ResponseEntity.ok("Family Member Deactivated");
    }

}
