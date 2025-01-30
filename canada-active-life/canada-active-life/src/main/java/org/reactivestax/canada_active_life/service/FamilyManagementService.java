package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.UUIDToken;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.exception.FamilyMemberNotFoundException;
import org.reactivestax.canada_active_life.mapper.FamilyMemberMapper;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.reactivestax.canada_active_life.repo.UUIDTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class FamilyManagementService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private UUIDTokenRepository uuidTokenRepository;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Transactional
    public boolean signUpNewFamilyMember(FamilyMemberDTO familyMemberDTO) {
        /**
         * createFamilyGroup - Done
         * createFamilyMember - with groupId created - isActive to false - Done
         * create UUIDToken and save this in the uuidTokenManagement table - Done
         * sendActivationLink with the UUIDToken - TODO
         */
        FamilyGroup createdFamilyGroup = createFamilyGroup(familyMemberDTO.getFamilyPin());

        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, createdFamilyGroup);
        createdFamilyGroup.setGroupOwner(createdFamilyMember);
        familyMemberRepository.save(createdFamilyMember);

        UUID uuidToken = createUUIDTokenForActivation(createdFamilyMember);
        return sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken);
    }

    private FamilyGroup createFamilyGroup(String familyPin) {
        FamilyGroup familyGroup = FamilyGroup.builder()
                .familyPin(familyPin)
                .build();
        return familyGroupRepository.save(familyGroup);
    }

    private FamilyMember createFamilyMember(FamilyMemberDTO familyMemberDTO, FamilyGroup createdFamilyGroup) {
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

    private UUID createUUIDTokenForActivation (FamilyMember createdFamilyMember) {
        UUID uuid  = UUID.randomUUID();
        UUIDToken uuidToken = UUIDToken.builder()
                .uuid(uuid)
                .familyMember(createdFamilyMember)
                .build();
        uuidTokenRepository.save(uuidToken);
        return uuid;
    }

    private boolean sendActivationLinkViaEms(int familyMemberId, UUID uuidToken) {
        log.info("Sending Activation Link Via Ems..." + familyMemberId + " " + uuidToken.toString());
        return true;
    }

    public boolean activateNewSignUp(int familyMemberId, UUID uuid){
        /**
         * map the familyMember and the UUID in the uuidTokenTable Table
         * once the match is found - get familyMember and set isActive true
         * check the group as well, if inactive, set to active
         * save familyMember
         */
        return false;
    }

    public boolean addFamilyMember(FamilyMemberDTO familyMemberDTO, String memberLoginId) {
        /**
         * checkActorValidity() - Done
         * Get the familyGroupId for the actor. - Done
         * Create a familyMember with the familyMemberDTO and set the FamilyGroupId as above. - Done
         * familyManagementRepository.save(familyMember) - Done
         *
         * Activation Link Same as SignUp - TODO
         */
        FamilyMember actor = checkActorValidity(memberLoginId);

        FamilyGroup familyGroupOfActor = actor.getFamilyGroup();
        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, familyGroupOfActor);
        familyMemberRepository.save(createdFamilyMember);

        UUID uuidToken = createUUIDTokenForActivation(createdFamilyMember);
        return sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken);
    }

    private FamilyMember checkActorValidity(String memberLoginId) {
        return familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Unable to find the actor of the request."));
    }

    public FamilyMemberDTO getFamilyMember(String memberLoginId) {
        /**
         * checkActorValidity() - Optional
         * familyManagementRepo.findById(familyMemberId)
         */
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family Member not found for the given Member Login Id."));

        return familyMemberMapper.toDto(familyMember);
    }

    public boolean updateFamilyMemberInfo(FamilyMemberDTO familyMemberDTO) {
        /**
         * checkActorValidity()
         * checkIfFamilyMemberExist
         * Update the changes in the familyMember extracted using JsonMerge and ObjectMapper
         */
        return false;
    }

    public boolean deactivateFamilyMember(String memberLoginId, String actorMemberLoginId) {
        /**
         * checkActorValidity() - For Security - Optional
         * checkIfFamilyMemberExist()
         * update the isActive field to inactive
         * save(FamilyMember)
         */
        return false;
    }
}
