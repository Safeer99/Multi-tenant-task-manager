package com.safeer.jiraclone.service.serviceInterfaces;

import com.safeer.jiraclone.dto.LoginUserDto;
import com.safeer.jiraclone.dto.RegisterUserResponseDto;
import com.safeer.jiraclone.entity.User;

public interface AuthServiceInter {

    RegisterUserResponseDto register(User user);

    String login(LoginUserDto loginUserDto);
}
