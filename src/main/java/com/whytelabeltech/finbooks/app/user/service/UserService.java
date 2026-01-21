package com.whytelabeltech.finbooks.app.user.service;

import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.Role;

public interface UserService {
    UserResponseDto register (UserRequestDto response);
}
