package com.ridelink.account.controller;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile & Status Management", description = "Endpoints for viewing and updating user profiles and account status (Owned by Member 1 - IT24102280)")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID", description = "Retrieves user details including role and account status")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Lists all registered users (admin operations)")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates name and phone number for an existing user")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update account status", description = "Allows suspending or reactivating an account (ACTIVE / SUSPENDED)")
    public ResponseEntity<UserProfileResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(userService.updateStatus(id, request));
    }
}
