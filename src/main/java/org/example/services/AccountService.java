package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.configuration.security.JwtService;
import org.example.constants.Roles;
import org.example.dto.account.AuthResponseDto;
import org.example.dto.account.LoginDto;
import org.example.dto.account.RegisterDto;
import org.example.entities.RoleEntity;
import org.example.entities.UserEntity;
import org.example.entities.UserRoleEntity;
import org.example.repositories.RoleRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.UserRoleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtService jwtService;

    public AuthResponseDto login(LoginDto request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isValid) {
            throw new UsernameNotFoundException("User not found");
        }
        var jwtToekn = jwtService.generateAccessToken(user);
        return AuthResponseDto.builder()
                .token(jwtToekn)
                .build();
    }

    public UserEntity register(RegisterDto request) {
        // Check if user with provided email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create a new user entity
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save the user entity to the database
        newUser = userRepository.save(newUser);

        // Find the role entity by name
        RoleEntity role = roleRepository.findByName(Roles.User);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }

        // Create a user role entity and associate it with the user
        UserRoleEntity userRole = UserRoleEntity.builder()
                .role(role)
                .user(newUser)
                .build();

        // Save the user role entity to the database
        userRoleRepository.save(userRole);

        return newUser;
    }

}
