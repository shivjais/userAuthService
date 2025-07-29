package com.learning.userauthservice.services;

import com.learning.userauthservice.exceptions.AuthServiceException;
import com.learning.userauthservice.models.User;
import com.learning.userauthservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;


    @Override
    public User signUp(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isPresent()){
            throw new AuthServiceException("User already exists. Please login.");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new AuthServiceException("User doesn't exists. Please signup.");
        }
        User user = userOptional.get();
        if(!user.getPassword().equals(password)){
            throw new AuthServiceException("Incorrect password.");
        }
        return user;
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        return null;
    }

    @Override
    public void logout(String token, Long userId) {

    }

    @Override
    public User forgotPassword(String email, String password) {
        return null;
    }
}
