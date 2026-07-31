package com.himpact.service;

import com.himpact.domain.events.CommentAddedEvent;
import com.himpact.dto.comment.AddCommentRequest;
import com.himpact.dto.comment.CommentResponse;
import com.himpact.entity.Comment;
import com.himpact.entity.Event;
import com.himpact.entity.Guest;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.CommentRepository;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Business logic service for Digital Congratulations Wall.
 * Emits CommentAddedEvent for loose module coupling.
 *
 * See: project-index/03_Functional_Requirements.md — FR-07 Gallery (comments)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Post a congratulatory wish on the event wall.
     */
    @Transactional
    public CommentResponse addComment(UUID eventId, AddCommentRequest request) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        Guest guest = guestRepository.findByInvitationCodeAndIsDeletedFalse(request.invitationCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "code", request.invitationCode()));

        Comment comment = Comment.builder()
                .event(event)
                .guest(guest)
                .message(request.message())
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Added wish from guest [{}] to event [{}]", guest.getFullName(), eventId);

        // Emit domain event asynchronously
        String snippet = request.message().length() > 50 ? request.message().substring(0, 50) + "..." : request.message();
        eventPublisher.publishEvent(new CommentAddedEvent(saved.getId(), eventId, guest.getId(), snippet));

        return mapToResponse(saved);
    }

    /**
     * Get paginated wishes for an event wall.
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(UUID eventId, Pageable pageable) {
        return commentRepository.findByEventIdAndIsDeletedFalse(eventId, pageable)
                .map(this::mapToResponse);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getGuest().getId(),
                comment.getGuest().getFullName(),
                comment.getMessage(),
                comment.getCreatedAt()
        );
    }
}
