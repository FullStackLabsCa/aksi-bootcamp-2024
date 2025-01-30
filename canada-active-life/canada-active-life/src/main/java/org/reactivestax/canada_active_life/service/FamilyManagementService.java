package org.reactivestax.canada_active_life.service;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyManagementService {

    public boolean signUpNewFamilyMember(FamilyMemberDTO familyMemberDTO) {
        /**
         * createFamilyGroup
         * createFamilyMember - with groupId created - isActive to false
         * create UUIDToken and save this in the signUpActivation table
         * sendActivationLink with the UUIDToken
         */
        return false;
    }

    public boolean activateNewSignUp(int familyMemberId, UUID uuid){
        /**
         * map the familyMember and the UUID in the signUpActivation Table
         * once the match is found - get familyMember and set isActive true
         * save familyMember
         */
        return false;
    }

    public boolean addFamilyMember(FamilyMemberDTO familyMemberDTO, Integer familyMemberId) {
        /**
         * checkActorValidity()
         * Get the familyGroupId for the actor.
         * Create a familyMember with the familyMemberDTO and set the FamilyGroupId as above.
         * familyManagementRepository.save(familyMember)
         *
         * OTP PseudoCode (Pending)
         */
        return false;
    }

    public Optional<FamilyMemberDTO> getFamilyMember(int familyMemberId) {
        /**
         * checkActorValidity()
         * familyManagementRepo.findById(familyMemberId)
         */
        return null;
    }

    public boolean updateFamilyMemberInfo(FamilyMemberDTO familyMemberDTO) {
        /**
         * checkActorValidity()
         * checkIfFamilyMemberExist
         * Update the changes in the familyMember extracted using JsonMerge and ObjectMapper
         */
        return false;
    }

    public boolean deactivateFamilyMember(int familyMemberId, Integer actorId) {
        /**
         * checkActorValidity() - For Security - Optional
         * checkIfFamilyMemberExist()
         * update the isActive field to inactive
         * save(FamilyMember)
         */
        return false;
    }
}
