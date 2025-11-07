package com.demo.updating_brain.prreview.dto;

public record FileChange(
        String filename,
        String status,
        int additions,
        int deletions,
        String patch
) {}
