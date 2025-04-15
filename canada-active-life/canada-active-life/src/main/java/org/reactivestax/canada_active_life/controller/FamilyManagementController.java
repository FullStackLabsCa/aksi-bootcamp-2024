package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/CanadaActiveLife/v1/members")
public class FamilyManagementController {

    @Autowired
    private FamilyManagementService familyManagementService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<String> addFamilyMemberToExistingGroup(@RequestBody FamilyMemberDTO familyMemberDTO, @RequestHeader("x-security-header") String actorId){

        boolean isFamilyMemberAdded = familyManagementService.addFamilyMember(familyMemberDTO, actorId);

        if(isFamilyMemberAdded) return ResponseEntity.ok("Family Member added to group.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be added to the group.");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<FamilyMemberDTO> getFamilyMemberDetails(@RequestParam String memberLoginId){
        FamilyMemberDTO familyMemberDTO = familyManagementService.getFamilyMember(memberLoginId);
        return ResponseEntity.ok(familyMemberDTO);
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FAMILY_MEMBER', 'ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<FamilyMemberDTO> updateFamilyMember(@RequestBody FamilyMemberDTO familyMemberDTO, @RequestParam String memberLoginId){
        FamilyMemberDTO updateFamilyMemberInfo = familyManagementService.updateFamilyMemberInfo(familyMemberDTO, memberLoginId);

        if(updateFamilyMemberInfo != null) return ResponseEntity.ok(updateFamilyMemberInfo);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<String> deactivateFamilyMember(@RequestParam String memberLoginId, @RequestHeader("x-security-header") String actorId){
        boolean isDeactivated = familyManagementService.deactivateFamilyMember(memberLoginId, actorId);

        if(isDeactivated) return ResponseEntity.ok("Family Member Deactivated");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be deactivated.");
    }

    @GetMapping("/activate")
    @PreAuthorize("hasAuthority('ROLE_FAMILY_GROUP_OWNER')")
    public ResponseEntity<String> activateFamilyMember(@RequestParam String memberLoginId, @RequestHeader("x-security-header") String actorId){
        boolean isActivated = familyManagementService.activateFamilyMember(memberLoginId, actorId);

        if(isActivated) return ResponseEntity.ok("Family Member Activated");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Family Member could not be Activated.");
    }

}
