package com.safeer.jiraclone.service;

import com.safeer.jiraclone.entity.User;
import com.safeer.jiraclone.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if(user == null){
            System.out.println("CUS: User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        return user;
    }
}
