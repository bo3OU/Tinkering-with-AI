package com.demo.updating_brain.prreview.dto;

import java.util.List;

public record PrReviewResult(
        String prNumber,
        String prTitle,
        String prAuthor,
        List<CodeIssue> issues,
        String notionPageUrl,
        String summary
) {}
