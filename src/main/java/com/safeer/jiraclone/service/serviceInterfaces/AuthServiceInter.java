package com.safeer.jiraclone.service.serviceInterfaces;

import com.safeer.jiraclone.dto.LoginUserDto;
import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.entity.User;

public interface AuthServiceInter {

    UserDetailsDto register(User user);

    String login(LoginUserDto loginUserDto);
}
