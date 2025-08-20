package com.learning.userauthservice.dtos;

import com.learning.userauthservice.models.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private List<Role> roles = new ArrayList<>();
}
