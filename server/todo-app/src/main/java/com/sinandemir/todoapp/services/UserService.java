package com.sinandemir.todoapp.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.repositories.UserRepository;

@Service
public class UserService {

    private UserRepository userRepos;

    public UserService(UserRepository userRepos) {
        this.userRepos = userRepos;
    }

    public Boolean existsByEmail(String email) {
        return userRepos.existsByEmail(email);
    }

    public Boolean existsByUsername(String username) {
        return userRepos.existsByUsername(username);
    }

    public User save(User user){
        return userRepos.save(user);
    }

    public Optional<User> findByUsernameOrEmail(String username, String email){
        return userRepos.findByUsernameOrEmail(username, email);
    }

    public Optional<User> findById(Long userId){
        return userRepos.findById(userId);
    }
}
