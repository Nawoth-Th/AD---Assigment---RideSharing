package com.ridelink.account.controller;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.exception.AccessDeniedException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.security.JwtTokenProvider;
import com.ridelink.account.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserController userController;

    private UserProfileResponse sampleProfile;

    @BeforeEach
    void setUp() {
        sampleProfile = UserProfileResponse.builder()
                .id("user-100")
                .name("Alice Smith")
                .email("alice@example.com")
                .phone("+94771234567")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("getUserById - Success")
    void getUserById_Success() {
        when(userService.getUserById("user-100")).thenReturn(sampleProfile);

        ResponseEntity<UserProfileResponse> response = userController.getUserById("user-100");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("alice@example.com", response.getBody().getEmail());
        verify(userService, times(1)).getUserById("user-100");
    }

    @Test
    @DisplayName("updateProfile - Success")
    void updateProfile_Success() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name("Alice Updated")
                .phone("+94779999999")
                .build();

        UserProfileResponse updatedProfile = UserProfileResponse.builder()
                .id("user-100")
                .name("Alice Updated")
                .email("alice@example.com")
                .phone("+94779999999")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(userService.updateProfile(eq("user-100"), any(UpdateProfileRequest.class))).thenReturn(updatedProfile);

        ResponseEntity<UserProfileResponse> response = userController.updateProfile("user-100", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice Updated", response.getBody().getName());
    }

    @Test
    @DisplayName("getAllUsers - Allowed with Admin Role Header")
    void getAllUsers_WithAdminRoleHeader_Success() {
        when(userService.getAllUsers()).thenReturn(List.of(sampleProfile));

        ResponseEntity<List<UserProfileResponse>> response = userController.getAllUsers(null, "ADMIN");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("getAllUsers - Allowed with Valid Admin JWT Bearer Token")
    void getAllUsers_WithAdminJwt_Success() {
        when(jwtTokenProvider.validateToken("admin-token")).thenReturn(true);
        when(jwtTokenProvider.getRoleFromToken("admin-token")).thenReturn("ADMIN");
        when(userService.getAllUsers()).thenReturn(List.of(sampleProfile));

        ResponseEntity<List<UserProfileResponse>> response = userController.getAllUsers("Bearer admin-token", null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("getAllUsers - Denied when Non-Admin Role Header provided")
    void getAllUsers_WithPassengerRoleHeader_ThrowsAccessDenied() {
        assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers(null, "PASSENGER"));
        verify(userService, never()).getAllUsers();
    }

    @Test
    @DisplayName("getAllUsers - Denied when No Auth Headers provided")
    void getAllUsers_NoHeaders_ThrowsAccessDenied() {
        assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers(null, null));
        verify(userService, never()).getAllUsers();
    }

    @Test
    @DisplayName("getAllUsers - Denied when JWT Token is Invalid")
    void getAllUsers_InvalidJwt_ThrowsAccessDenied() {
        when(jwtTokenProvider.validateToken("bad-token")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers("Bearer bad-token", null));
        verify(userService, never()).getAllUsers();
    }

    @Test
    @DisplayName("getAllUsers - Denied when JWT Token Role is PASSENGER")
    void getAllUsers_PassengerJwt_ThrowsAccessDenied() {
        when(jwtTokenProvider.validateToken("passenger-token")).thenReturn(true);
        when(jwtTokenProvider.getRoleFromToken("passenger-token")).thenReturn("PASSENGER");

        assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers("Bearer passenger-token", null));
        verify(userService, never()).getAllUsers();
    }

    @Test
    @DisplayName("updateStatus - Allowed with Admin Role Header")
    void updateStatus_WithAdminRoleHeader_Success() {
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(AccountStatus.SUSPENDED)
                .build();

        UserProfileResponse suspendedProfile = UserProfileResponse.builder()
                .id("user-100")
                .name("Alice Smith")
                .email("alice@example.com")
                .phone("+94771234567")
                .role(Role.PASSENGER)
                .status(AccountStatus.SUSPENDED)
                .createdAt(Instant.now())
                .build();

        when(userService.updateStatus(eq("user-100"), any(UpdateStatusRequest.class))).thenReturn(suspendedProfile);

        ResponseEntity<UserProfileResponse> response = userController.updateStatus("user-100", request, null, "ADMIN");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AccountStatus.SUSPENDED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("updateStatus - Denied with Passenger Role Header")
    void updateStatus_WithPassengerRoleHeader_ThrowsAccessDenied() {
        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(AccountStatus.SUSPENDED)
                .build();

        assertThrows(AccessDeniedException.class, () ->
                userController.updateStatus("user-100", request, null, "PASSENGER"));
        verify(userService, never()).updateStatus(anyString(), any());
    }
}
