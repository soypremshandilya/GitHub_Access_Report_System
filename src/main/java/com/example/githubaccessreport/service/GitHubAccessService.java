package com.example.githubaccessreport.service;

import com.example.githubaccessreport.client.GitHubApiClient;
import com.example.githubaccessreport.model.Collaborator;
import com.example.githubaccessreport.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubAccessService {

    private final GitHubApiClient apiClient;

    public Map<String, List<String>> generateAccessReport() {
        log.info("Starting access report generation for org: {}", apiClient.getOrganization());
        
        List<Repository> repositories = apiClient.getRepositories();
        log.info("Found {} repositories", repositories.size());

        Map<String, List<String>> userAccessMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = repositories.stream()
                .map(repo -> CompletableFuture.runAsync(() -> {
                    try {
                        List<Collaborator> collaborators = apiClient.getCollaborators(repo.getName());
                        for (Collaborator collab : collaborators) {
                            userAccessMap.computeIfAbsent(collab.getLogin(), k -> new CopyOnWriteArrayList<>())
                                         .add(repo.getName());
                        }
                    } catch (Exception e) {
                        log.error("Failed to fetch collaborators for repo {}: {}", repo.getName(), e.getMessage());
                    }
                }))
                .collect(Collectors.toList());

        // Wait for all async tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Access report generation complete. Found {} users.", userAccessMap.size());
        return userAccessMap;
    }
}
