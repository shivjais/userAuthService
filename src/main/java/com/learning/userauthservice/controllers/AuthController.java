package com.learning.userauthservice.controllers;

import com.learning.userauthservice.dtos.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignupRequestDto signupRequestDto){
        return null;
    }

    //post because we are going to create JWT token in this call
    @PostMapping("/login")
    public UserDto login(@RequestBody LoginRequestDto loginRequestDto){
        return null;
    }

    //post because we will create object of JWT Parser
    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequest validateTokenRequest){
        return false;
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto logoutRequestDto){

    }

    @PostMapping("/forgetPassword")
    public UserDto forgetPassword(@RequestBody LoginRequestDto loginRequestDto){
        return null;
    }
}
