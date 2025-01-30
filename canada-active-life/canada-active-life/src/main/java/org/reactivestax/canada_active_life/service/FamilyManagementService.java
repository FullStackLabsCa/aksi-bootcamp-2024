package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.UUIDTokenManagement;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FamilyManagementService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Transactional
    public boolean signUpNewFamilyMember(FamilyMemberDTO familyMemberDTO) {
        /**
         * createFamilyGroup - Done
         * createFamilyMember - with groupId created - isActive to false - Done
         * create UUIDToken and save this in the uuidTokenManagement table - Done
         * sendActivationLink with the UUIDToken - TODO
         */
        FamilyGroup createdFamilyGroup = createNewFamilyGroup(familyMemberDTO.getFamilyPin());
        FamilyMember createdFamilyMember = createNewFamilyMember(familyMemberDTO, createdFamilyGroup);
        UUID uuidToken = createUUIDTokenForActivation(createdFamilyMember);
        familyMemberRepository.save(createdFamilyMember);
        sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken);
        return false;
    }

    private FamilyGroup createNewFamilyGroup(String familyPin) {
        return FamilyGroup.builder()
                .familyPin(familyPin)
                .build();
    }

    private FamilyMember createNewFamilyMember(FamilyMemberDTO familyMemberDTO, FamilyGroup createdFamilyGroup) {
        return FamilyMember.builder()
                .name(familyMemberDTO.getName())
                .dob(familyMemberDTO.getDob())
                .gender(familyMemberDTO.getGender())
                .emailAddress(familyMemberDTO.getEmailAddress())
                .streetNumber(familyMemberDTO.getStreetNumber())
                .streetName(familyMemberDTO.getStreetName())
                .city(familyMemberDTO.getCity())
                .province(familyMemberDTO.getProvince())
                .country(familyMemberDTO.getCountry())
                .homePhoneNumber(familyMemberDTO.getHomePhoneNumber())
                .businessPhoneNumber(familyMemberDTO.getBusinessPhoneNumber())
                .preferredContactMethod(familyMemberDTO.getPreferredContactMethod())
                .language(familyMemberDTO.getLanguage())
                .memberLoginId(familyMemberDTO.getMemberLoginId())
                .familyGroup(createdFamilyGroup)
                .build();
    }

    private void sendActivationLinkViaEms(int familyMemberId, UUID uuidToken) {
        log.info("Sending Activation Link Via Ems..." + familyMemberId + " " + uuidToken.toString());
    }

    private UUID createUUIDTokenForActivation (FamilyMember createdFamilyMember) {
        UUID uuid  = UUID.randomUUID();
        UUIDTokenManagement.builder()
                .uuid(uuid)
                .familyMember(createdFamilyMember)
                .build();
        return uuid;
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
