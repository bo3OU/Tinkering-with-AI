package com.demo.updating_brain.geopost.prreview.dto;

import java.util.List;
import java.util.Map;

public record NotionPageRequest(
        Map<String, Object> parent,
        Map<String, Object> properties,
        List<Map<String, Object>> children
) {}
