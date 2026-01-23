package com.whytelabeltech.finbooks.app.user.service;

import com.whytelabeltech.finbooks.app.email.service.MailManager;
import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.dto.request.UpdateUserDto;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.Role;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthenticationException;
import com.whytelabeltech.finbooks.middleware.exception.error.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailManager mailManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");
        user.setRole(Role.USER);
    }

    @Test
    void testRegister_Success() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponseDto response = userService.register(request);

        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        verify(mailManager, times(1)).sendSuccessfulRegistrationEmail(any(EmailBuilderRequest.class));
    }

    @Test
    void testRegister_DuplicateEmail() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Email already Registered");
    }

    @Test
    void testRegister_DuplicateUsername() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Username already exist");
    }

    @Test
    void testGetOne_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getOne(1L);

        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testGetOne_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getOne(99L))
                .isInstanceOf(UserException.class)
                .hasMessage("User not found");
    }

    @Test
    void testUpdateUser_Success() {
        setupSecurityContextForUpdate("testuser");

        UpdateUserDto request = new UpdateUserDto();
        request.setUsername("testuser");
        request.setEmail("updated@example.com");
        request.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updatedUser = userService.updateUser(1L, request);

        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getPassword()).isNull();
        assertThat(updatedUser.getOtp()).isNull();
    }

    @Test
    void testUpdateUser_Unauthorized() {
        setupSecurityContextForUpdate("anotheruser");

        UpdateUserDto request = new UpdateUserDto();
        request.setUsername("newuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(UserException.class)
                .hasMessage("Details can only be updated by user");
    }

    @Test
    void testUpdateUser_DuplicateEmail() {
        setupSecurityContextForUpdate("testuser");

        UpdateUserDto request = new UpdateUserDto();
        request.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Email already exist for another user");
    }

    @Test
    void testUpdateUser_DuplicateUsername() {
        setupSecurityContextForUpdate("testuser");

        UpdateUserDto request = new UpdateUserDto();
        request.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Username already exist for another user");
    }

    @Test
    void testDeleteUser_Success_ByOwner() {
        setupSecurityContextForDelete("testuser", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        MessageResponse response = userService.deleteUser(1L);

        assertThat(response.getMessage()).isEqualTo("User Deleted Successfully");
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_Success_ByAdmin() {
        setupSecurityContextForDelete("anotheruser", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        MessageResponse response = userService.deleteUser(1L);

        assertThat(response.getMessage()).isEqualTo("User Deleted Successfully");
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_Unauthorized() {
        setupSecurityContextForDelete("anotheruser", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserException.class)
                .hasMessage("User can only be deleted by the owner or an Admin ");
    }

    @Test
    void testDeleteUser_NotFound() {
        setupSecurityContextForDelete("testuser", false);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserException.class)
                .hasMessage("user not found");
    }

    private void setupSecurityContextForUpdate(String username) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
    }

    private void setupSecurityContextForDelete(String username, boolean isAdmin) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        if (isAdmin) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        } else {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
            when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        }
    }
}