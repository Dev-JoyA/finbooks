package com.whytelabeltech.finbooks.app.user.service;

import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.dto.request.UpdateUserDto;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.User;

public interface UserService {
    UserResponseDto register (UserRequestDto request);
    UserResponseDto getOne (Long userId);
    User updateUser (Long userId, UpdateUserDto request);
    MessageResponse deleteUser (Long userId);
}
