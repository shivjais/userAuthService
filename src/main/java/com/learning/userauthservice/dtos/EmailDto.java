package com.learning.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailDto {
    private String from;
    private String to;
    private String subject;
    private String body;
}
