package com.example.githubaccessreport.controller;

import com.example.githubaccessreport.service.GitHubAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/access-report")
@RequiredArgsConstructor
public class GitHubAccessController {

    private final GitHubAccessService githubAccessService;

    @GetMapping
    public Map<String, List<String>> getAccessReport() {
        return githubAccessService.generateAccessReport();
    }
}
