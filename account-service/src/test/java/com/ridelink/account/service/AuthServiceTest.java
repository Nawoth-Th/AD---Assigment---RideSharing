package com.ridelink.account.service;

import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.TokenValidationRequest;
import com.ridelink.account.dto.TokenValidationResponse;
import com.ridelink.account.exception.AccountSuspendedException;
import com.ridelink.account.exception.EmailAlreadyExistsException;
import com.ridelink.account.exception.InvalidCredentialsException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("user-123")
                .name("John Doe")
                .email("john@example.com")
                .passwordHash("encodedPassword")
                .phone("+94771234567")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .build();

        registerRequest = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("plainSecret123")
                .phone("+94771234567")
                .role(Role.PASSENGER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("plainSecret123")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainSecret123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), any(Role.class))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.PASSENGER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException on duplicate email")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully authenticate user with valid credentials")
    void testLoginSuccess() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainSecret123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(sampleUser.getId(), sampleUser.getEmail(), sampleUser.getRole()))
                .thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("mock-jwt-token", response.getToken());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void testLoginInvalidPassword() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongSecret", "encodedPassword")).thenReturn(false);

        LoginRequest wrongReq = LoginRequest.builder()
                .email("john@example.com")
                .password("wrongSecret")
                .build();

        assertThrows(InvalidCredentialsException.class, () -> authService.login(wrongReq));
    }

    @Test
    @DisplayName("Should throw AccountSuspendedException when account is suspended")
    void testLoginSuspendedUser() {
        sampleUser.setStatus(AccountStatus.SUSPENDED);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainSecret123", "encodedPassword")).thenReturn(true);

        assertThrows(AccountSuspendedException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Should validate valid JWT token")
    void testValidateTokenSuccess() {
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid-token")).thenReturn("user-123");
        when(jwtTokenProvider.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(jwtTokenProvider.getRoleFromToken("valid-token")).thenReturn("PASSENGER");
        when(userRepository.findById("user-123")).thenReturn(Optional.of(sampleUser));

        TokenValidationResponse response = authService.validateToken(new TokenValidationRequest("valid-token"));

        assertTrue(response.isValid());
        assertEquals("user-123", response.getUserId());
        assertEquals(Role.PASSENGER, response.getRole());
    }
}
