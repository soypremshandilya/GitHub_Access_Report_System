package com.example.githubaccessreport.client;

import com.example.githubaccessreport.model.Collaborator;
import com.example.githubaccessreport.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubApiClient {

    private final WebClient webClient;

    @Value("${github.org}")
    private String organization;

    @Cacheable(value = "github-repos", key = "#root.target.organization")
    public List<Repository> getRepositories() {
        List<Repository> allRepos = new ArrayList<>();
        int page = 1;
        while (true) {
            log.info("Fetching repositories for org: {} (Page {})", organization, page);
            List<Repository> repos = webClient.get()
                    .uri("/orgs/{org}/repos?per_page=100&page={page}", organization, page)
                    .retrieve()
                    .bodyToFlux(Repository.class)
                    .collectList()
                    .block();
            
            if (repos == null || repos.isEmpty()) {
                break;
            }
            allRepos.addAll(repos);
            if (repos.size() < 100) {
                break;
            }
            page++;
        }
        return allRepos;
    }

    @Cacheable(value = "github-collabs", key = "#repoName")
    public List<Collaborator> getCollaborators(String repoName) {
        List<Collaborator> allCollaborators = new ArrayList<>();
        int page = 1;
        while (true) {
            log.info("Fetching collaborators for repo: {}/{} (Page {})", organization, repoName, page);
            List<Collaborator> collabs = webClient.get()
                    .uri("/repos/{owner}/{repo}/collaborators?per_page=100&page={page}", organization, repoName, page)
                    .retrieve()
                    .bodyToFlux(Collaborator.class)
                    .collectList()
                    .block();
            
            if (collabs == null || collabs.isEmpty()) {
                break;
            }
            allCollaborators.addAll(collabs);
            if (collabs.size() < 100) {
                break;
            }
            page++;
        }
        return allCollaborators;
    }

    public String getOrganization() {
        return organization;
    }
}
