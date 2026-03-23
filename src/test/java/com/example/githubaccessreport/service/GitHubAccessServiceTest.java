package com.example.githubaccessreport.service;

import com.example.githubaccessreport.client.GitHubApiClient;
import com.example.githubaccessreport.model.Collaborator;
import com.example.githubaccessreport.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubAccessServiceTest {

    @Mock
    private GitHubApiClient apiClient;

    @InjectMocks
    private GitHubAccessService service;

    @BeforeEach
    void setUp() {
        lenient().when(apiClient.getOrganization()).thenReturn("test-org");
    }

    @Test
    void testGenerateAccessReport_Success() {
        Repository repo1 = new Repository(1L, "repo1");
        Repository repo2 = new Repository(2L, "repo2");
        
        when(apiClient.getRepositories()).thenReturn(List.of(repo1, repo2));
        
        Collaborator user1 = new Collaborator(10L, "user1");
        Collaborator user2 = new Collaborator(20L, "user2");
        
        when(apiClient.getCollaborators("repo1")).thenReturn(List.of(user1, user2));
        when(apiClient.getCollaborators("repo2")).thenReturn(List.of(user1));

        Map<String, List<String>> report = service.generateAccessReport();

        assertNotNull(report);
        assertEquals(2, report.size());
        
        assertTrue(report.containsKey("user1"));
        assertEquals(2, report.get("user1").size());
        assertTrue(report.get("user1").containsAll(List.of("repo1", "repo2")));

        assertTrue(report.containsKey("user2"));
        assertEquals(1, report.get("user2").size());
        assertTrue(report.get("user2").contains("repo1"));
    }
}
