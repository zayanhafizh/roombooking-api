package com.polstat.roombooking.controller;

import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.entity.Role;
import com.polstat.roombooking.entity.RoleType;
import com.polstat.roombooking.repository.RoleRepository;
import com.polstat.roombooking.repository.UserRepository;
import com.polstat.roombooking.service.UserService;
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

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PutMapping("/update-role/{userId}")
    public Map<String, String> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> roleRequest) {
        String role = roleRequest.get("role");

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Hanya bisa mengubah ke peran "ADMIN"
        if (role.equalsIgnoreCase("SUPERADMIN")) {
            throw new IllegalArgumentException("Only ADMIN and USER role can be assigned");
        }

        // Dapatkan peran "ADMIN" dari database
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role ADMIN not found"));

        // Perbarui peran pengguna
        user.setRole(adminRole);
        userRepository.save(user);

        // Kembalikan respons sukses
        Map<String, String> response = new HashMap<>();
        response.put("message", "User role updated to " +role+ " successfully");
        return response;
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
