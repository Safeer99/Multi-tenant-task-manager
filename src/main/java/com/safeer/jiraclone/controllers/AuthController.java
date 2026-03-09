package com.safeer.jiraclone.controllers;

import com.safeer.jiraclone.dto.LoginUserDto;
import com.safeer.jiraclone.dto.RegisterUserResponseDto;
import com.safeer.jiraclone.entity.User;
import com.safeer.jiraclone.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> register(@RequestBody User user){
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto){
        return ResponseEntity.ok(authService.login(loginUserDto));
    }
}
