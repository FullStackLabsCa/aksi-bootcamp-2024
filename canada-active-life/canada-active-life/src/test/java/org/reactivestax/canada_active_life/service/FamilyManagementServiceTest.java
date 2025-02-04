package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestax.canada_active_life.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.mapper.FamilyMemberMapper;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.reactivestax.canada_active_life.repo.PendingLoginUUIDRepository;
import org.reactivestax.canada_active_life.repo.PendingSignUpUUIDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertFalse(familyManagementService.signUpNewFamilyMember(null));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
        verify(familyGroupRepository, times(0)).save(any(FamilyGroup.class));
        verify(pendingSignUpUUIDRepository, times(0)).save(any(PendingSignUpUUID.class));
    }

    @Test
    void testSignUpNewFamilyMember_InvalidFamilyMemberDTO(){
        FamilyMemberDTO familyMemberDTO = FamilyManagementTestDataProvider.invalidFamilyMemberDTO.get();

        assertFalse(familyManagementService.signUpNewFamilyMember(familyMemberDTO));
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
        verify(familyGroupRepository, times(1)).save(any(FamilyGroup.class));
        verify(pendingSignUpUUIDRepository, times(1)).save(any(PendingSignUpUUID.class));
    }

    @Test
    void testSignUpNewFamilyMember_ValidFamilyMemberDTO(){
        /**
         * FamilyMemberRepo.save() - 1
         * FamilyGroupRepo.save() - 1
         * UUID Creation Verification - pendingSignUpUUIDRepository.save() - 1
         * sendActivationLinkVerification - return true assertion
         */
        // Setup
        FamilyMemberDTO familyMemberDTO = FamilyManagementTestDataProvider.goodFamilyMemberDTO.get();
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Message Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        // Action and Assert
        assertTrue(familyManagementService.signUpNewFamilyMember(familyMemberDTO));
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
        verify(familyGroupRepository, times(1)).save(any(FamilyGroup.class));
        verify(pendingSignUpUUIDRepository, times(1)).save(any(PendingSignUpUUID.class));
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
