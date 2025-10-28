package com.workflow.auth.service;

import com.workflow.auth.dto.LoginRequest;
import com.workflow.auth.dto.LoginResponse;
import com.workflow.auth.model.User;
import com.workflow.auth.repository.UserRepository;
import com.workflow.auth.security.JwtUtils;
import com.workflow.auth.security.UserPrincipal;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @PostConstruct
    public void init() {
        logger.info("=================================");
        logger.info("AuthService initialized!");
        logger.info("=================================");
    }
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            String role = userPrincipal.getAuthorities().isEmpty() ? "ROLE_USER" : 
                         userPrincipal.getAuthorities().iterator().next().getAuthority();
            
            return new LoginResponse(jwt, userPrincipal.getId(), userPrincipal.getUsername(), 
                    userPrincipal.getEmail(), role);
        } catch (Exception e) {
            logger.error("Error in authenticateUser: {}", e.getMessage());
            throw e;
        }
    }
    
    public User createDefaultUser() {
        if (!userRepository.existsByUsername("admin")) {
            User user = new User("admin", "admin@workflow.com", encoder.encode("admin123"));
            user.setRole(com.workflow.auth.model.Role.ADMIN);
            user.setCamundaUserId("admin");
            return userRepository.save(user);
        }
        return userRepository.findByUsername("admin").orElse(null);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
