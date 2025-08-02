package com.inptrental.inptrental.security;

import com.inptrental.inptrental.model.Student;
import com.inptrental.inptrental.repository.StudentRepository;
import com.inptrental.inptrental.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StudentRepository studentRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String subject = jwtService.extractSubject(token); // student ID as subject
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                long studentId = Long.parseLong(subject);
                studentRepository.findById(studentId).ifPresent(student -> {
                    if (jwtService.isTokenValid(token, subject) && student.isEmailVerified()) {
                        // store the Student as principal (you can also store inemail if you prefer)
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                student,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                });
            }
        } catch (Exception e) {
            // invalid token / parsing error / etc. — swallow so request stays unauthenticated
            // optionally log at debug level: logger.debug("JWT auth failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
