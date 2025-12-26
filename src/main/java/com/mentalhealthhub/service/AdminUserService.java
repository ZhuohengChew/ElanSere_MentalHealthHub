package com.mentalhealthhub.service;

import com.mentalhealthhub.dto.CreateUserDto;
import com.mentalhealthhub.dto.UpdateUserDto;
import com.mentalhealthhub.model.AuditLog;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AuditLogRepository;
import com.mentalhealthhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(CreateUserDto dto, Long adminId) {
        if (dto == null) throw new IllegalArgumentException("Invalid user data");
        if (dto.email == null || !dto.email.contains("@")) throw new IllegalArgumentException("Invalid email");
        if (userRepository.findByEmail(dto.email).isPresent()) throw new IllegalArgumentException("Email already exists");

        if (dto.password == null || dto.password.length() < 6) throw new IllegalArgumentException("Password too short");

        UserRole role = UserRole.STUDENT;
        try { role = UserRole.valueOf(dto.role.toUpperCase()); } catch (Exception ignored) {}

        User u = new User();
        u.setEmail(dto.email.trim().toLowerCase());
        u.setName(dto.name != null ? dto.name.trim() : dto.email);
        u.setPassword(passwordEncoder.encode(dto.password));
        u.setRole(role);
        u.setActive(dto.active == null ? true : dto.active);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(u);
        
        // Log the action
        AuditLog log = new AuditLog(adminId, "USER_CREATED", saved.getId(), 
            "Created user: " + dto.email + " with role " + role.name());
        auditLogRepository.save(log);
        
        return saved;
    }

    public void changeStatus(Long id, boolean active, Long adminId) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
        User u = opt.get();
        boolean wasActive = u.getActive();
        u.setActive(active);
        u.setUpdatedAt(LocalDateTime.now());
        userRepository.save(u);
        
        // Log the action
        String action = active ? "USER_ACTIVATED" : "USER_DEACTIVATED";
        AuditLog log = new AuditLog(adminId, action, id, 
            "Changed status from " + wasActive + " to " + active);
        auditLogRepository.save(log);
    }

    public void archiveUser(Long id, Long adminId) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
        User u = opt.get();
        u.setDeletedAt(LocalDateTime.now());
        u.setActive(false);
        u.setUpdatedAt(LocalDateTime.now());
        userRepository.save(u);
        
        // Log the action
        AuditLog log = new AuditLog(adminId, "USER_ARCHIVED", id, 
            "Archived user: " + u.getEmail());
        auditLogRepository.save(log);
    }

    public String resetPassword(Long id, Long adminId) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
        User u = opt.get();
        String newPass = UUID.randomUUID().toString().replaceAll("[-]","").substring(0,10);
        u.setPassword(passwordEncoder.encode(newPass));
        u.setUpdatedAt(LocalDateTime.now());
        userRepository.save(u);
        
        // Log the action
        AuditLog log = new AuditLog(adminId, "PASSWORD_RESET", id, 
            "Reset password for user: " + u.getEmail());
        auditLogRepository.save(log);
        
        return newPass;
    }

    public User updateUser(Long id, UpdateUserDto dto, Long adminId) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
        User u = opt.get();
        
        StringBuilder details = new StringBuilder("Updated user: ");
        boolean changed = false;
        
        if (dto.name != null && !dto.name.trim().isEmpty() && !u.getName().equals(dto.name)) {
            details.append("name '").append(u.getName()).append("' -> '").append(dto.name).append("'; ");
            u.setName(dto.name.trim());
            changed = true;
        }
        if (dto.email != null && dto.email.contains("@") && !u.getEmail().equals(dto.email)) {
            details.append("email '").append(u.getEmail()).append("' -> '").append(dto.email).append("'; ");
            u.setEmail(dto.email.trim().toLowerCase());
            changed = true;
        }
        if (dto.role != null && !u.getRole().name().equals(dto.role)) {
            try {
                UserRole newRole = UserRole.valueOf(dto.role.toUpperCase());
                details.append("role '").append(u.getRole().name()).append("' -> '").append(newRole.name()).append("'; ");
                u.setRole(newRole);
                changed = true;
            } catch (Exception ignored) {}
        }
        if (dto.profilePicture != null) {
            u.setProfilePicture(dto.profilePicture);
            changed = true;
        }
        
        u.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(u);
        
        // Log the action only if something changed
        if (changed) {
            AuditLog log = new AuditLog(adminId, "USER_UPDATED", id, details.toString());
            auditLogRepository.save(log);
        }
        
        return saved;
    }
}

