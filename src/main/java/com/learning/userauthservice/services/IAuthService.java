package com.learning.userauthservice.services;

import com.learning.userauthservice.models.User;

public interface IAuthService {
    User signUp(String name, String email, String password, String phoneNumber);

    User login(String email, String password);

    Boolean validateToken(String token,Long userId);

    void logout(String token,Long userId);

    User forgotPassword(String email, String password);
}
