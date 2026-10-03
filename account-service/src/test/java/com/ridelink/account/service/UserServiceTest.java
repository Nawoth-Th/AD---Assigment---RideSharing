package com.ridelink.account.service;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.exception.ResourceNotFoundException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user-123")
                .name("Alice Smith")
                .email("alice@example.com")
                .phone("+94779876543")
                .role(Role.DRIVER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should return user profile by ID")
    void testGetUserByIdSuccess() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(sampleUser));

        UserProfileResponse response = userService.getUserById("user-123");

        assertNotNull(response);
        assertEquals("user-123", response.getId());
        assertEquals("Alice Smith", response.getName());
        assertEquals(Role.DRIVER, response.getRole());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for unknown ID")
    void testGetUserByIdNotFound() {
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById("non-existent"));
    }

    @Test
    @DisplayName("Should update user profile")
    void testUpdateProfile() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest("Alice Updated", "+94770000000");
        UserProfileResponse response = userService.updateProfile("user-123", request);

        assertEquals("Alice Updated", response.getName());
        assertEquals("+94770000000", response.getPhone());
    }

    @Test
    @DisplayName("Should update account status to SUSPENDED")
    void testUpdateStatus() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest(AccountStatus.SUSPENDED);
        UserProfileResponse response = userService.updateStatus("user-123", request);

        assertEquals(AccountStatus.SUSPENDED, response.getStatus());
    }
}
