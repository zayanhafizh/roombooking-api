package com.polstat.roombooking.controller;

import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.entity.Role;
import com.polstat.roombooking.entity.RoleType;
import com.polstat.roombooking.repository.RoleRepository;
import com.polstat.roombooking.repository.UserRepository;
import com.polstat.roombooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Operation(summary = "Update user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid role", content = @Content)
    })

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PutMapping("/update-role/{userId}")
    public Map<String, String> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> roleRequest) {
        String role = roleRequest.get("role");

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (role.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Only ADMIN and USER role can be assigned");
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role ADMIN not found"));

        user.setRole(adminRole);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User role updated to " + role + " successfully");
        return response;
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
