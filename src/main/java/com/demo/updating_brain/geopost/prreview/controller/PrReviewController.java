package com.demo.updating_brain.geopost.prreview.controller;

import com.demo.updating_brain.geopost.prreview.dto.PrReviewResult;
import com.demo.updating_brain.geopost.prreview.service.PrReviewTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pr-review")
@Tag(name = "PR Review Controller", description = "AI-powered pull request code review with automatic fix PR creation and Notion integration")
public class PrReviewController {

    private final ChatClient chatClient;
    private final PrReviewTools prReviewTools;

    public PrReviewController(ChatClient.Builder builder, PrReviewTools prReviewTools) {
        this.chatClient = builder.build();
        this.prReviewTools = prReviewTools;
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze latest PR", description = "Fetches the latest open PR from configured repository, performs AI code review to find bugs and errors, creates a Notion page with results, and automatically creates a fix PR with suggested corrections")
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

                        CRITICAL OUTPUT REQUIREMENT - READ THIS CAREFULLY:
                        Your ENTIRE response must be ONLY a single valid JSON object.

                        START your response with { (opening brace)
                        END your response with } (closing brace)

                        JSON structure:
                        {
                          "prNumber": "string",
                          "prTitle": "string",
                          "prAuthor": "string",
                          "issues": [
                            {
                              "severity": "HIGH|MEDIUM|LOW",
                              "description": "string",
                              "problematicCode": "string",
                              "suggestedFix": "string",
                              "fileName": "string",
                              "lineNumber": number
                            }
                          ],
                          "notionPageUrl": "string",
                          "summary": "string"
                        }

                        ABSOLUTELY FORBIDDEN:
                        - Do NOT write "Based on" or any other introductory text
                        - Do NOT write "Here is" or "Let me create"
                        - Do NOT write explanations before or after the JSON
                        - Do NOT use markdown code blocks (```)
                        - Do NOT add any text outside the JSON object

                        Your response must START with { and END with }
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

                        Step 6: If issues were found, call create_fix_pull_request with the PR number and the list of issues to automatically create a fix PR

                        Step 7: FINAL STEP - Return the JSON response

                        STOP AND READ THIS:
                        You MUST return ONLY a JSON object. Nothing else.

                        Your response must be EXACTLY:
                        {
                          "prNumber": "the PR number as a string",
                          "prTitle": "the PR title",
                          "prAuthor": "the PR author",
                          "issues": [...all CodeIssues found...],
                          "notionPageUrl": "the URL from create_notion_review_page",
                          "summary": "your analysis summary"
                        }

                        THE FIRST CHARACTER of your response must be {
                        THE LAST CHARACTER of your response must be }

                        DO NOT WRITE:
                        - "Based on my analysis..."
                        - "Here is the JSON..."
                        - "Let me create..."
                        - Any text before the {
                        - Any text after the }

                        Just the JSON. Start with { now.

                        If no issues are found, return an empty issues array with a positive summary.
                        """)
                .call()
                .entity(PrReviewResult.class);
    }
}
