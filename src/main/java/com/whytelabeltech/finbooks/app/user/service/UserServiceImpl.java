package com.whytelabeltech.finbooks.app.user.service;

import com.whytelabeltech.finbooks.app.email.service.MailManager;
import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.dto.request.UpdateUserDto;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthenticationException;
import com.whytelabeltech.finbooks.middleware.exception.error.UserException;
import com.whytelabeltech.finbooks.middleware.security.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier(value = "brevo")
    private MailManager mailManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @Override
    public UserResponseDto register(UserRequestDto request) {

        if (request == null) {
            throw new AuthenticationException("Invalid registration request");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthenticationException("Email already Registered");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AuthenticationException("Username already exist");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());


        userRepository.save(user);

        EmailBuilderRequest emailBuilderRequest = new EmailBuilderRequest();
        emailBuilderRequest.setTo(user.getEmail());
        emailBuilderRequest.setProps(new HashMap<>());
        emailBuilderRequest.getProps().put("username", user.getUsername());
        mailManager.sendSuccessfulRegistrationEmail(emailBuilderRequest);


        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


    @Override
    public UserResponseDto getOne (Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();


    }

    @Override
    @Transactional
    public User updateUser (Long userId, UpdateUserDto request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("user not found"));

        if(!user.getUsername().equalsIgnoreCase(currentUsername)){
            throw new UserException("Details can only be updated by user");
        }

        if(request.getEmail() != null){
            if (userRepository.findByEmail(request.getEmail())
                    .filter(u -> !u.getId().equals(userId)).isPresent()) {
                throw new AuthenticationException("Email already exist for another user");
            }
            user.setEmail(request.getEmail());
        }

        if(request.getUsername() != null){
            if (userRepository.findByUsername(request.getUsername())
                    .filter(u -> !u.getId().equals(userId)).isPresent()) {
                throw new AuthenticationException("Username already exist for another user");
            }
            user.setUsername(request.getUsername());
        }

        if(request.getPassword() != null){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        user.setOtp(null);
        user.setPassword(null);

        return user;
    }

    @Override
    @Transactional
    public MessageResponse deleteUser (Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean roles = authentication.getAuthorities().stream().anyMatch(role ->
                role.getAuthority().equals("ADMIN"));


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("user not found"));

        if(!user.getUsername().equalsIgnoreCase(currentUsername) && !roles){
            throw new UserException("User can only be deleted by the owner or an Admin ");
        }
        userRepository.deleteById(userId);
        return new MessageResponse("User Deleted Successfully");
    }
}
