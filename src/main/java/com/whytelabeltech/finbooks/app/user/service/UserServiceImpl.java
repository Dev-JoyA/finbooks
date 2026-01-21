package com.whytelabeltech.finbooks.app.user.service;

import com.whytelabeltech.finbooks.app.email.service.MailManager;
import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.Role;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthenticationException;
import com.whytelabeltech.finbooks.middleware.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));;
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
}
