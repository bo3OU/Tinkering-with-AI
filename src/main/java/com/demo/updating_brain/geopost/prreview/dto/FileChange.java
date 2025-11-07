package com.demo.updating_brain.geopost.prreview.dto;

public record FileChange(
        String filename,
        String status,
        int additions,
        int deletions,
        String patch
) {}
