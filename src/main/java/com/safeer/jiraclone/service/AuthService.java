package com.safeer.jiraclone.service;

import com.safeer.jiraclone.dto.LoginUserDto;
import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.entity.User;
import com.safeer.jiraclone.repository.UserRepository;
import com.safeer.jiraclone.service.serviceInterfaces.AuthServiceInter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthServiceInter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationContext context;

    @Override
    public UserDetailsDto register(User signUpUser){

        User user = userRepository.findByUsername(signUpUser.getUsername());

        if(user != null) throw new IllegalArgumentException("User already exists");

        signUpUser.setPassword(passwordEncoder.encode(signUpUser.getPassword()));

        user = userRepository.save(signUpUser);

        return modelMapper.map(user, UserDetailsDto.class);
    }

    @Override
    public String login(LoginUserDto loginUserDto) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginUserDto.getUsername(), loginUserDto.getPassword()
                ));

        User newUser = (User) authentication.getPrincipal();

        if(authentication.isAuthenticated() && newUser != null){
            return jwtService.generateToken(newUser);
        }

        throw new IllegalArgumentException("Invalid User");
    }

    public UserDetailsDto getCurrentUser(HttpHeaders headers){
        String authHeader = headers.getFirst("Authorization");
        String token = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if(token != null && username != null){
            return modelMapper.map(userRepository.findByUsername(username), UserDetailsDto.class);
        }

        throw new IllegalArgumentException("Invalid token");
    }
}
