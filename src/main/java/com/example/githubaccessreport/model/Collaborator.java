package com.example.githubaccessreport.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collaborator {
    private Long id;
    private String login;
}
