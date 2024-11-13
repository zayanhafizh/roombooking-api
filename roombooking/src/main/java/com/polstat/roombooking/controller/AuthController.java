package com.polstat.roombooking.controller;

import com.polstat.roombooking.auth.JwtUtil;
import com.polstat.roombooking.entity.Identity;
import com.polstat.roombooking.entity.Role;
import com.polstat.roombooking.entity.RoleType;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.repository.RoleRepository;
import com.polstat.roombooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register endpoint
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> registrationRequest) {
        String email = registrationRequest.get("email");
        String password = registrationRequest.get("password");

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));

        // Create new user without identity details
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(userRole);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    // Login endpoint
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(email, user.getRole().getName().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getName());

        return response;
    }

    // Get Profile endpoint
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public Map<String, Object> getProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("email", user.getEmail());

        if (user.getIdentity() != null) {
            profile.put("nama", user.getIdentity().getNama());
            profile.put("nim", user.getIdentity().getNim());
            profile.put("kelas", user.getIdentity().getKelas());
        } else {
            profile.put("nama", "");
            profile.put("nim", "");
            profile.put("kelas", "");
        }

        return profile;
    }

    // Update Profile endpoint (for adding/updating nama, nim, and kelas)
    @PatchMapping("/profile")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public Map<String, String> updateProfile(@RequestBody Map<String, String> profileRequest, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Identity identity = user.getIdentity();
        if (identity == null) {
            identity = new Identity();
            user.setIdentity(identity);
        }

        identity.setNama(profileRequest.get("nama"));
        identity.setNim(profileRequest.get("nim"));
        identity.setKelas(profileRequest.get("kelas"));

        userRepository.save(user); // Save the user with updated or new identity

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        return response;
    }
}
