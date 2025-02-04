package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.exception.FamilyMemberNotFoundException;
import org.reactivestax.canada_active_life.exception.InvalidSignUpActivationLinkException;
import org.reactivestax.canada_active_life.exception.MemberNotActivatedException;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        // Setup
        when(pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(PendingSignUpUUID.builder().build()));
        when(familyMemberRepository.findByFamilyMemberId(any(Integer.class)))
                .thenReturn(Optional.empty());

        // Action and Assert
        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.activateNewSignUp(1, UUID.randomUUID()));
        verify(pendingSignUpUUIDRepository, times(1)).findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class));
        verify(familyMemberRepository, times(1)).findByFamilyMemberId(any(Integer.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testActivateNewSignUpLink_InvalidUUIDTokenCombination(){
        // Setup
        when(pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Action and Assert
        assertThrows(InvalidSignUpActivationLinkException.class, () -> familyManagementService.activateNewSignUp(1, UUID.randomUUID()));
        verify(pendingSignUpUUIDRepository, times(1)).findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class));
        verify(familyMemberRepository, times(0)).findByFamilyMemberId(any(Integer.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testActivateNewSignUpLink_ValidCombination(){
        /**
         * 1 call to pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc
         * 1 call to familyMemberRepository.findByFamilyMemberId
         * 1 call to familyMemberRepository.save
         * assertTrue;
         */
        // Setup
        when(pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(PendingSignUpUUID.builder().build()));
        when(familyMemberRepository.findByFamilyMemberId(any(Integer.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodInactiveFamilyMember.get()));

        // Action and Assert
        assertTrue(familyManagementService.activateNewSignUp(1, UUID.randomUUID()));
        verify(pendingSignUpUUIDRepository, times(1)).findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(Integer.class), any(LocalDateTime.class));
        verify(familyMemberRepository, times(1)).findByFamilyMemberId(any(Integer.class));
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
    }

    @Test
    void testAddFamilyMember_InvalidActor(){
        // Setup
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.empty());

        // Action and Assert
        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.addFamilyMember(FamilyManagementTestDataProvider.goodFamilyMemberDTO.get(), "anyActor"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingSignUpUUIDRepository, times(0)).save(any(PendingSignUpUUID.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testAddFamilyMember_InactiveActor(){
        // Setup
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodInactiveFamilyMember.get()));

        // Action and Assert
        assertThrows(MemberNotActivatedException.class, () -> familyManagementService.addFamilyMember(FamilyManagementTestDataProvider.goodFamilyMemberDTO.get(), "anyActor"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingSignUpUUIDRepository, times(1)).save(any(PendingSignUpUUID.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testAddFamilyMember_ValidActor(){
        /**
         * 1 Call - familyMemberRepository.findByMemberLoginId(
         * 1 Call - familyMemberRepository.save
         * activation Link - assert True
         */
        // Setup
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodInactiveFamilyMember.get()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Message Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        // Action and Assert
        assertTrue(familyManagementService.addFamilyMember(FamilyManagementTestDataProvider.goodFamilyMemberDTO.get(), "anyActor"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
        verify(pendingSignUpUUIDRepository, times(1)).save(any(PendingSignUpUUID.class));
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
