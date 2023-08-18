package com.sinandemir.todoapp.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
}
