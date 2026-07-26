package com.himpact.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for building the standard API response envelope.
 *
 * Standard success:  { "success": true, "message": "...", "data": {...} }
 * Standard error:    { "success": false, "errorCode": "...", "message": "..." }
 *
 * See: project-index/07_API_Specification.md — API Response Format
 */
public final class ApiResponse {

    private ApiResponse() {}

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }
}
