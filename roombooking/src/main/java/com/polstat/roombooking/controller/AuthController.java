package com.polstat.roombooking.controller;

import com.polstat.roombooking.auth.JwtUtil;
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

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> registrationRequest) {
        String email = registrationRequest.get("email");
        String password = registrationRequest.get("password");

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }

        // Periksa apakah pengguna sudah ada
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Enkripsi password
        String encodedPassword = passwordEncoder.encode(password);

        // Tetapkan peran "USER" secara default
        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));

        // Buat dan simpan pengguna baru
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(userRole);

        userRepository.save(user);

        // Kembalikan respons sukses
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    // Endpoint Login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Autentikasi pengguna
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Ambil pengguna dari database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Buat token JWT dengan username dan role
        String token = jwtUtil.generateToken(email, user.getRole().getName().name());

        // Siapkan respons berisi token, email, dan role pengguna
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getName());

        return response; // Pastikan ini dikembalikan
    }

    // Endpoint untuk mengubah kata sandi
    @PutMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public Map<String, String> changePassword(@RequestBody Map<String, String> passwordRequest, Authentication authentication) {
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");

        // Ambil email pengguna yang sedang login
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verifikasi password lama
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update dengan password baru
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Kembalikan respons sukses
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return response;
    }
}
