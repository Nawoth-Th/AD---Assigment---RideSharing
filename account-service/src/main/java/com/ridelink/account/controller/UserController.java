package com.ridelink.account.controller;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.exception.AccessDeniedException;
import com.ridelink.account.security.JwtTokenProvider;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Profile & Status Management", description = "Endpoints for viewing and updating user profiles and account status (Owned by Member 1 - IT24102280)")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Step 04 - Get user profile by ID", description = "Retrieves user details including role and account status")
    public ResponseEntity<UserProfileResponse> getUserById(
            @Parameter(description = "User ID", example = "6ad14827495a0211f7495faf") @PathVariable("id") String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Step 05 - Update user profile", description = "Updates name and phone number for an existing user")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Parameter(description = "User ID", example = "6ad14827495a0211f7495faf") @PathVariable("id") String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    @GetMapping
    @Operation(summary = "Step 06 - Get all users (Admin operation)", description = "Lists all registered users. Requires ADMIN role or Bearer token.")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(
            @Parameter(description = "Optional JWT Bearer Token (or click Authorize at top)") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Optional Role Header (e.g. ADMIN)", example = "ADMIN") @RequestHeader(value = "X-User-Role", required = false) String roleHeader) {
        verifyAdminAccess(authHeader, roleHeader);
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Step 07 - Update account status (Admin operation)", description = "Allows suspending or reactivating an account (ACTIVE / SUSPENDED). Requires ADMIN role or Bearer token.")
    public ResponseEntity<UserProfileResponse> updateStatus(
            @Parameter(description = "User ID", example = "6ad14827495a0211f7495faf") @PathVariable("id") String id,
            @Valid @RequestBody UpdateStatusRequest request,
            @Parameter(description = "Optional JWT Bearer Token (or click Authorize at top)") @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Optional Role Header (e.g. ADMIN)", example = "ADMIN") @RequestHeader(value = "X-User-Role", required = false) String roleHeader) {
        verifyAdminAccess(authHeader, roleHeader);
        return ResponseEntity.ok(userService.updateStatus(id, request));
    }

    private void verifyAdminAccess(String authHeader, String roleHeader) {
        if (roleHeader != null && !roleHeader.isBlank()) {
            if ("ADMIN".equalsIgnoreCase(roleHeader.trim())) {
                return;
            }
            throw new AccessDeniedException("Access denied: Administrative privileges (ADMIN role) required.");
        }

        if (authHeader != null && !authHeader.isBlank()) {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7).trim() : authHeader.trim();
            if (!jwtTokenProvider.validateToken(token)) {
                throw new AccessDeniedException("Access denied: Invalid or expired authentication token.");
            }
            String role = jwtTokenProvider.getRoleFromToken(token);
            if ("ADMIN".equalsIgnoreCase(role)) {
                return;
            }
            throw new AccessDeniedException("Access denied: Administrative privileges (ADMIN role) required.");
        }

        throw new AccessDeniedException("Access denied: Administrative privileges (ADMIN role) required.");
    }
}
