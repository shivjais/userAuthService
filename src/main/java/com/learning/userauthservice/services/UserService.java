package com.learning.userauthservice.services;

import com.learning.userauthservice.models.User;
import com.learning.userauthservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User getUserById(Long id){
        User user = userRepo.findById(id).get();
        System.out.println("User Details: "+user.getEmail());
        return user;
    }
}
