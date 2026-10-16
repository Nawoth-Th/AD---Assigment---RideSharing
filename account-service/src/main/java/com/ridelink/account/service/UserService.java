package com.ridelink.account.service;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.exception.ResourceNotFoundException;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getUserById(String id) {

        log.info("Fetching user profile for id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToProfileResponse(user);
    }

    public List<UserProfileResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    public UserProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        log.info("Updating user profile for id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    public UserProfileResponse updateStatus(String id, UpdateStatusRequest request) {
        log.info("Updating user status for id: {} to {}", id, request.getStatus());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(request.getStatus());
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
