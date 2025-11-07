package com.demo.updating_brain.prreview.dto;

public record CodeIssue(
        String severity,
        String description,
        String problematicCode,
        String suggestedFix,
        String fileName,
        Integer lineNumber
) {}
