package com.learning.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequest {
    private String token;
    //it might be possible the token (not JWT) of two user will be same
    private Long userId;
}
