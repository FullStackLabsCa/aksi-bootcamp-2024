package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.mapper.FamilyMemberMapper;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.reactivestax.canada_active_life.repo.PendingLoginUUIDRepository;
import org.reactivestax.canada_active_life.repo.PendingSignUpUUIDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class FamilyManagementServiceTest {

    @Autowired
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private FamilyMemberRepository familyMemberRepository;

    @MockitoBean
    private FamilyGroupRepository familyGroupRepository;

    @MockitoBean
    private PendingSignUpUUIDRepository pendingSignUpUUIDRepository;

    @MockitoBean
    private PendingLoginUUIDRepository pendingLoginUUIDRepository;

    @MockitoBean
    private FamilyMemberMapper familyMemberMapper;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testSignUpNewFamilyMember_NullFamilyMemberDTO(){

    }

    @Test
    void testSignUpNewFamilyMember_InvalidFamilyMemberDTO(){

    }

    @Test
    void testSignUpNewFamilyMember_ValidFamilyMemberDTO(){
        /**
         * FamilyMemberRepo.save() - 1
         * FamilyGroupRepo.save() - 1
         * UUID Creation Verification - pendingSignUpUUIDRepository.save() - 1
         * sendActivationLinkVerification - return true assertion
         */
    }

    @Test
    void testActivationLink_WrongPhoneNumberForEms(){

    }

    @Test
    void testActivateNewSignUpLink_InvalidFamilyMemberId(){

    }

    @Test
    void testActivateNewSignUpLink_InvalidUUIDTokenCombination(){

    }

    @Test
    void testActivateNewSignUpLink_ValidCombination(){
        /**
         * 1 call to pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc
         * 1 call to familyMemberRepository.findByFamilyMemberId
         * 1 call to familyMemberRepository.save
         * assertTrue;
         */
    }

    @Test
    void testAddFamilyMember_InvalidActor(){

    }

    @Test
    void testAddFamilyMember_InactiveActor(){

    }

    @Test
    void testAddFamilyMember_ValidActor(){
        /**
         * 1 Call - familyMemberRepository.findByMemberLoginId(
         * 1 Call - familyMemberRepository.save
         * activation Link - assert True
         */
    }

    @Test
    void testCheckFamilyMemberValidity_ValidMemberLoginId(){

    }

    @Test
    void testCheckFamilyMemberValidity_InvalidMemberLoginId(){

    }

    @Test
    void testGetFamilyMember_ValidMemberLoginId(){

    }

    @Test
    void testGetFamilyMember_InvalidMemberLoginId(){

    }

    @Test
    void testUpdateFamilyMember_(){}

    @Test
    void testDeactivateFamilyMember_InvalidActor(){

    }

    @Test
    void testDeactivateFamilyMember_UnauthorizedActor(){

    }

    @Test
    void testDeactivateFamilyMember_InvalidMember(){

    }

    @Test
    void testDeactivateFamilyMember_ValidActorAndValidMember(){
        /**
         * 2 Call - familyMemberRepository.findByMemberLoginId
         * 1 Call - familyMemberRepository.save
         * assert True;
         */
    }

    @Test
    void testLoginMember_InvalidMemberLoginId(){

    }

    @Test
    void testLoginMember_InvalidFamilyPin(){

    }

    @Test
    void testLoginMember_InactiveMember(){

    }

    @Test
    void testMemberLogin_ValidCredentials(){
        /**
         * 1 Call - familyMemberRepository.findByMemberLoginId
         * 1 Call - UUIDLogin.save
         * assertNotNull(UUIDTokenForLogin)
         */
    }

    @Test
    void testMemberLoginVerification_InvalidUUIDIdentifier(){

    }

    @Test
    void testMemberLoginVerification_InvalidOTP(){

    }

    @Test
    void testMemberLoginVerification_ValidOTP(){

    }
}
