package com.demo.updating_brain.prreview.service;

import com.demo.updating_brain.prreview.dto.CodeIssue;
import com.demo.updating_brain.prreview.dto.FileChange;
import com.demo.updating_brain.prreview.dto.PrReviewResult;
import com.demo.updating_brain.prreview.dto.PullRequestData;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PrReviewTools {

    private final RestClient restClient;

    @Value("${github.api.token:}")
    private String githubToken;

    @Value("${github.repository.owner:}")
    private String repoOwner;

    @Value("${github.repository.name:}")
    private String repoName;

    @Value("${notion.api.token:}")
    private String notionToken;

    @Value("${notion.page.id:}")
    private String notionPageId;

    public PrReviewTools() {
        this.restClient = RestClient.create();
    }

    @Tool(name = "fetch_latest_pull_request", description = "Fetches the latest open pull request from the configured GitHub repository with all file changes and diffs")
    public PullRequestData fetchLatestPullRequest() {
        if (githubToken == null || githubToken.isEmpty()) {
            throw new RuntimeException("GitHub token not configured");
        }

        try {
            // Fetch latest open PR
            String prUrl = String.format("https://api.github.com/repos/%s/%s/pulls?state=open&sort=created&direction=desc&per_page=1",
                    repoOwner, repoName);

            List<Map<String, Object>> prs = restClient.get()
                    .uri(prUrl)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (prs == null || prs.isEmpty()) {
                throw new RuntimeException("No open pull requests found");
            }

            Map<String, Object> pr = prs.get(0);
            int prNumber = (Integer) pr.get("number");
            String title = (String) pr.get("title");
            String body = (String) pr.get("body");
            Map<String, Object> user = (Map<String, Object>) pr.get("user");
            String author = (String) user.get("login");

            // Fetch PR files
            String filesUrl = String.format("https://api.github.com/repos/%s/%s/pulls/%d/files",
                    repoOwner, repoName, prNumber);

            List<Map<String, Object>> filesData = restClient.get()
                    .uri(filesUrl)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            List<FileChange> files = new ArrayList<>();
            if (filesData != null) {
                for (Map<String, Object> file : filesData) {
                    files.add(new FileChange(
                            (String) file.get("filename"),
                            (String) file.get("status"),
                            (Integer) file.get("additions"),
                            (Integer) file.get("deletions"),
                            (String) file.getOrDefault("patch", "")
                    ));
                }
            }

            return new PullRequestData(prNumber, title, author, body, files);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch PR from GitHub: " + e.getMessage(), e);
        }
    }

    @Tool(name = "create_notion_review_page", description = "Creates a new child page in Notion under the configured parent page with the PR review results including all issues found and suggested fixes")
    public String createNotionReviewPage(PrReviewResult review) {
        if (notionToken == null || notionToken.isEmpty()) {
            return "Notion token not configured. Please set notion.api.token in application.properties";
        }

        try {
            // Build Notion page content
            List<Map<String, Object>> children = new ArrayList<>();

            // Add summary section
            children.add(createHeading2("Summary"));
            children.add(createParagraph(review.summary()));

            // Add issues section
            children.add(createHeading2("Issues Found (" + review.issues().size() + ")"));

            for (CodeIssue issue : review.issues()) {
                // Issue header
                children.add(createHeading3(issue.severity() + ": " + issue.description()));

                // File and line info
                children.add(createParagraph("File: " + issue.fileName() + " (Line: " + issue.lineNumber() + ")"));

                // Problematic code
                children.add(createParagraph("Problematic Code:"));
                children.add(createCodeBlock(issue.problematicCode()));

                // Suggested fix
                children.add(createParagraph("Suggested Fix:"));
                children.add(createCodeBlock(issue.suggestedFix()));

                // Divider
                children.add(createDivider());
            }

            // Build request for child page
            Map<String, Object> parent = Map.of("page_id", notionPageId);

            Map<String, Object> properties = Map.of(
                    "title", List.of(
                            Map.of("text", Map.of("content", "PR #" + review.prNumber() + ": " + review.prTitle()))
                    )
            );

            Map<String, Object> requestBody = Map.of(
                    "parent", parent,
                    "properties", properties,
                    "children", children
            );

            String url = "https://api.notion.com/v1/pages";

            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + notionToken)
                    .header("Notion-Version", "2022-06-28")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null && response.get("url") != null) {
                return (String) response.get("url");
            } else {
                return "Failed to create Notion page: " + response;
            }

        } catch (Exception e) {
            return "Error creating Notion page: " + e.getMessage();
        }
    }

    // Helper methods for Notion blocks
    private Map<String, Object> createHeading2(String text) {
        return Map.of(
                "object", "block",
                "type", "heading_2",
                "heading_2", Map.of(
                        "rich_text", List.of(Map.of("text", Map.of("content", text)))
                )
        );
    }

    private Map<String, Object> createHeading3(String text) {
        return Map.of(
                "object", "block",
                "type", "heading_3",
                "heading_3", Map.of(
                        "rich_text", List.of(Map.of("text", Map.of("content", text)))
                )
        );
    }

    private Map<String, Object> createParagraph(String text) {
        return Map.of(
                "object", "block",
                "type", "paragraph",
                "paragraph", Map.of(
                        "rich_text", List.of(Map.of("text", Map.of("content", text)))
                )
        );
    }

    private Map<String, Object> createCodeBlock(String code) {
        return Map.of(
                "object", "block",
                "type", "code",
                "code", Map.of(
                        "rich_text", List.of(Map.of("text", Map.of("content", code))),
                        "language", "java"
                )
        );
    }

    private Map<String, Object> createDivider() {
        return Map.of(
                "object", "block",
                "type", "divider",
                "divider", Map.of()
        );
    }
}
