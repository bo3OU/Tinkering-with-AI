package com.demo.updating_brain.prreview.controller;

import com.demo.updating_brain.prreview.dto.PrReviewResult;
import com.demo.updating_brain.prreview.service.PrReviewTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pr-review")
@Tag(name = "PR Review Controller", description = "AI-powered pull request code review with Notion integration")
public class PrReviewController {

    private final ChatClient chatClient;
    private final PrReviewTools prReviewTools;

    public PrReviewController(ChatClient.Builder builder, PrReviewTools prReviewTools) {
        this.chatClient = builder.build();
        this.prReviewTools = prReviewTools;
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze latest PR", description = "Fetches the latest open PR from configured repository, performs AI code review to find bugs and errors, suggests fixes, and creates a Notion page with results")
    public PrReviewResult analyzeLatestPr() {
        return chatClient.prompt()
                .system("""
                        You are an expert code reviewer focused on finding bugs, errors, and potential issues in code.

                        Your goal is to identify:
                        - Logical errors that will cause bugs
                        - Potential runtime errors (null pointer, array out of bounds, etc.)
                        - Code that will fail or crash
                        - Incorrect implementations that don't match intended behavior

                        For each issue you find, provide:
                        1. Clear description of the problem
                        2. The exact problematic code
                        3. A concrete fix with corrected code

                        Be specific and actionable in your suggestions.

                        IMPORTANT: Return ONLY raw JSON without markdown formatting, code blocks, or backticks.
                        """)
                .tools(prReviewTools)
                .user("""
                        Task: Review the latest pull request and create a Notion page with findings

                        Step 1: Call fetch_latest_pull_request to get the PR data with all file changes

                        Step 2: Carefully analyze each file change and diff:
                        - Look for bugs, logical errors, and potential runtime errors
                        - Examine the code for incorrect implementations
                        - Check for error-prone patterns
                        - Identify code that could fail or crash

                        Step 3: For each issue found, create a CodeIssue with:
                        - severity: "HIGH", "MEDIUM", or "LOW"
                        - description: Clear explanation of what's wrong
                        - problematicCode: The exact code snippet with the issue
                        - suggestedFix: The corrected code
                        - fileName: The file where the issue is
                        - lineNumber: The line number (if available from patch)

                        Step 4: Create a summary of the review with:
                        - Overall assessment
                        - Total number of issues found
                        - Main concerns

                        Step 5: Call create_notion_review_page with the PrReviewResult to create the Notion page

                        Step 6: Return the complete PrReviewResult including:
                        - prNumber
                        - prTitle
                        - prAuthor
                        - issues (list of all CodeIssues found)
                        - notionPageUrl (URL returned from Notion)
                        - summary

                        If no issues are found, return an empty issues list with a positive summary.
                        """)
                .call()
                .entity(PrReviewResult.class);
    }
}
