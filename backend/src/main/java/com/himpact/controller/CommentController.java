package com.himpact.controller;

import com.himpact.dto.PageResponse;
import com.himpact.dto.comment.AddCommentRequest;
import com.himpact.dto.comment.CommentResponse;
import com.himpact.service.CommentService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Digital Congratulations Wall Controller.
 * Base path: /api/v1/events/{eventId}/comments
 * See: project-index/07_API_Specification.md — Comment APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/comments")
@RequiredArgsConstructor
@Tag(name = "Digital Wishes", description = "Post and view congratulations wall messages")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Post Wish", description = "Public guest endpoint for leaving a congratulatory message.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable UUID eventId,
            @Valid @RequestBody AddCommentRequest request
    ) {
        CommentResponse response = commentService.addComment(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wish posted successfully.", response));
    }

    @Operation(summary = "Get Wishes Wall", description = "Public endpoint for viewing digital wishes wall.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getComments(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentResponse> comments = commentService.getComments(eventId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Wishes wall retrieved.", PageResponse.from(comments)));
    }
}
