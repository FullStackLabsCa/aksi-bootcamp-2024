package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.PendingLoginUUID;
import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.exception.*;
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

import java.lang.reflect.Member;
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
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodActiveFamilyMember.get()));
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
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodActiveFamilyMember.get()));

        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity("anyFamilyMember");
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        assertNotNull(familyMember);
    }

    @Test
    void testCheckFamilyMemberValidity_InvalidMemberLoginId(){
        // Setup
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.empty());

        // Action and Assert
        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.checkFamilyMemberValidity("anyFamilyMember"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
    }

    @Test
    void testGetFamilyMember_ValidMemberLoginId(){
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMember.get()));
        when(familyMemberMapper.toDto(any(FamilyMember.class))).thenReturn(FamilyManagementTestDataProvider.goodFamilyMemberDTO.get());

        FamilyMemberDTO familyMemberDTO = familyManagementService.getFamilyMember("anyFamilyMember");
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        assertNotNull(familyMemberDTO);
    }

    @Test
    void testGetFamilyMember_InvalidMemberLoginId(){
        // Setup
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.empty());

        // Action and Assert
        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.getFamilyMember("anyFamilyMember"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
    }

    @Test
    void testUpdateFamilyMember_(){}

    @Test
    void testDeactivateFamilyMember_InvalidActor(){
        when(familyMemberRepository.findByMemberLoginId("anyActor"))
                .thenReturn(Optional.empty());

        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.deactivateFamilyMember("anyMember", "anyActor"));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testDeactivateFamilyMember_UnauthorizedActor(){
        when(familyMemberRepository.findByMemberLoginId("anyActor"))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get()));
        when(familyMemberRepository.findByMemberLoginId("anyMember"))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner2.get()));

        assertThrows(ActorNotAuthorizedException.class, () -> familyManagementService.deactivateFamilyMember("anyMember", "anyActor"));
        verify(familyMemberRepository, times(2)).findByMemberLoginId(any(String.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testDeactivateFamilyMember_InvalidMember(){
        when(familyMemberRepository.findByMemberLoginId("anyActor"))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get()));
        when(familyMemberRepository.findByMemberLoginId("anyMember"))
                .thenReturn(Optional.empty());

        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.deactivateFamilyMember("anyMember", "anyActor"));
        verify(familyMemberRepository, times(2)).findByMemberLoginId(any(String.class));
        verify(familyMemberRepository, times(0)).save(any(FamilyMember.class));
    }

    @Test
    void testDeactivateFamilyMember_ValidActorAndValidMember(){
        /**
         * 2 Call - familyMemberRepository.findByMemberLoginId
         * 1 Call - familyMemberRepository.save
         * assert True;
         */
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get()));

        assertTrue(familyManagementService.deactivateFamilyMember("anyMember", "anyActor"));
        verify(familyMemberRepository, times(2)).findByMemberLoginId(any(String.class));
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
    }

    @Test
    void testLoginMember_InvalidMemberLoginId(){
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(FamilyMemberNotFoundException.class, () -> familyManagementService.loginMember(FamilyManagementTestDataProvider.userLoginDTO.get()));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingSignUpUUIDRepository, times(0)).save(any(PendingSignUpUUID.class));
        verify(pendingLoginUUIDRepository, times(0)).save(any(PendingLoginUUID.class));
    }

    @Test
    void testLoginMember_InvalidFamilyPin(){
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get()));

        UUID uuid = familyManagementService.loginMember(FamilyManagementTestDataProvider.userLoginDTO.get());
        assertNull(uuid);
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingSignUpUUIDRepository, times(0)).save(any(PendingSignUpUUID.class));
        verify(pendingLoginUUIDRepository, times(0)).save(any(PendingLoginUUID.class));
    }

    @Test
    void testLoginMember_InactiveMember(){
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodInactiveFamilyMember.get()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Message Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        assertThrows(MemberNotActivatedException.class, () -> familyManagementService.loginMember(FamilyManagementTestDataProvider.userLoginDTO.get()));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingSignUpUUIDRepository, times(1)).save(any(PendingSignUpUUID.class));
        verify(pendingLoginUUIDRepository, times(0)).save(any(PendingLoginUUID.class));
    }

    @Test
    void testLoginMember_SendOtpViaEmsFailed(){
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodActiveFamilyMember.get()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("No OTP Sent.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        assertThrows(FailedToSendOtpException.class, () -> familyManagementService.loginMember(FamilyManagementTestDataProvider.userLoginDTO.get()));
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingLoginUUIDRepository, times(1)).save(any(PendingLoginUUID.class));
    }

    @Test
    void testMemberLogin_ValidCredentials(){
        /**
         * 1 Call - familyMemberRepository.findByMemberLoginId
         * 1 Call - UUIDLogin.save
         * assertNotNull(UUIDTokenForLogin)
         */
        when(familyMemberRepository.findByMemberLoginId(any(String.class)))
                .thenReturn(Optional.of(FamilyManagementTestDataProvider.goodActiveFamilyMember.get()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("OTP Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);


        UUID uuid = familyManagementService.loginMember(FamilyManagementTestDataProvider.userLoginDTO.get());
        assertNotNull(uuid);
        verify(familyMemberRepository, times(1)).findByMemberLoginId(any(String.class));
        verify(pendingLoginUUIDRepository, times(1)).save(any(PendingLoginUUID.class));
    }

    @Test
    void testMemberLoginVerification_InvalidUUIDIdentifier(){
        when(pendingLoginUUIDRepository.findTopByUuidAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(UUIDTokenInvalidException.class, () -> familyManagementService.loginVerification(FamilyManagementTestDataProvider.loginVerificationDTO.get(), UUID.randomUUID()));
    }

    @Test
    void testMemberLoginVerification_InvalidOTP(){
        when(pendingLoginUUIDRepository.findTopByUuidAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(PendingLoginUUID.builder()
                        .familyMember(FamilyMember.builder().familyMemberId(1).build())
                        .build()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Customer Not Verified", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        assertFalse(familyManagementService.loginVerification(FamilyManagementTestDataProvider.loginVerificationDTO.get(), UUID.randomUUID()));
    }

    @Test
    void testMemberLoginVerification_ValidOTP(){
        when(pendingLoginUUIDRepository.findTopByUuidAndCreationTimeStampAfterOrderByCreationTimeStampDesc(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(PendingLoginUUID.builder()
                                .familyMember(FamilyMember.builder().familyMemberId(1).build())
                        .build()));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Customer Verified", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        assertTrue(familyManagementService.loginVerification(FamilyManagementTestDataProvider.loginVerificationDTO.get(), UUID.randomUUID()));
    }
}
