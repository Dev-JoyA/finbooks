package com.whytelabeltech.finbooks.app.user.controller;

import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.dto.request.UpdateUserDto;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> registerUser (
            @RequestBody UserRequestDto request
    ) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponseDto> getOne (
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getOne(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser (
            @PathVariable Long id,
            @RequestBody UpdateUserDto request
            ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> deleteUser (
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }



}
