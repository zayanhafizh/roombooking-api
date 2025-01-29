package com.polstat.roombooking.controller;

import com.polstat.roombooking.dto.UserDTO;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Endpoint untuk mendapatkan daftar semua pengguna
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all users retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();  // Mengambil semua pengguna

            // Convert list of users to list of UserDTO
            List<UserDTO> userDTOs = users.stream().map(user -> {
                String role = user.getRole() != null ? String.valueOf(user.getRole().getName()) : "";
                String nama = user.getIdentity() != null ? user.getIdentity().getNama() : "N/A";  // Penanganan null
                String kelas = user.getIdentity() != null ? user.getIdentity().getKelas() : "N/A";  // Penanganan null
                String nim = user.getIdentity() != null ? user.getIdentity().getNim() : "N/A";  // Penanganan null
                return new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        role,
                        nama,
                        kelas,
                        nim
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(userDTOs);  // Mengirimkan daftar UserDTO sebagai response
        } catch (Exception e) {
            // Jika ada error dalam mengambil data pengguna
            return ResponseEntity.status(500).build();
        }
    }

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

        // Cegah perubahan ke SUPERADMIN
        if (role.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Only ADMIN and USER roles can be assigned");
        }

        // Temukan role yang benar dari database (bisa ADMIN atau USER)
        Role newRole = roleRepository.findByName(RoleType.valueOf(role.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Role " + role + " not found"));

        // Update role pengguna
        user.setRole(newRole);
        userRepository.save(user);

        // Response sukses
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
