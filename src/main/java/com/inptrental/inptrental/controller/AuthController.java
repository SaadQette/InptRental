package com.inptrental.inptrental.controller;

import com.inptrental.inptrental.dto.AuthResponseDto;
import com.inptrental.inptrental.dto.LoginDto;
import com.inptrental.inptrental.dto.RegisterDto;
import com.inptrental.inptrental.model.Student;
import com.inptrental.inptrental.repository.StudentRepository;
import com.inptrental.inptrental.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto, HttpServletRequest req) {
        if (studentRepository.findByInemail(dto.getInemail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Inemail already in use");
        }

        Student student = new Student();
        student.setFullName(dto.getFullName());
        student.setInemail(dto.getInemail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        student.setEmailVerified(false);

        String verificationToken = UUID.randomUUID().toString();
        student.setVerificationToken(verificationToken);
        student.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        studentRepository.save(student);

        // Build verification link (adjust host/port if different)
        String baseUrl = req.getRequestURL().toString().replace(req.getRequestURI(), "");
        String link = String.format("%s/auth/verify?token=%s&inemail=%s",
                baseUrl,
                URLEncoder.encode(verificationToken, StandardCharsets.UTF_8),
                URLEncoder.encode(dto.getInemail(), StandardCharsets.UTF_8));

        // TODO: send real email; for now log the link so you can copy/paste it
        System.out.println("EMAIL VERIFICATION LINK (dev): " + link);

        return ResponseEntity.ok("Registered. Check console (or email) for verification link.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token, @RequestParam String inemail) {
        Student student = studentRepository.findByInemail(inemail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (student.isEmailVerified()) {
            return ResponseEntity.ok("Already verified");
        }

        if (!token.equals(student.getVerificationToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }

        if (student.getVerificationTokenExpiry() == null || student.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
        }

        student.setEmailVerified(true);
        student.setVerificationToken(null);
        student.setVerificationTokenExpiry(null);
        studentRepository.save(student);

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto dto) {
        Student student = studentRepository.findByInemail(dto.getInemail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        if (!student.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not verified");
        }

        if (!passwordEncoder.matches(dto.getPassword(), student.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        String token = jwtService.generateToken(student.getId().toString());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}
