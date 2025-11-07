package com.demo.updating_brain.geopost.prreview.dto;

import java.util.List;

public record PullRequestData(
        int number,
        String title,
        String author,
        String body,
        List<FileChange> files
) {}
