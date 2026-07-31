package com.himpact.service;

import com.himpact.dto.PageResponse;
import com.himpact.entity.AuditLog;
import com.himpact.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for recording immutable administrative audit logs.
 * Never overwrites audit history.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void recordAudit(UUID userId, String ipAddress, String action, String entityName, String entityId, String oldValue, String newValue) {
        AuditLog audit = AuditLog.builder()
                .userId(userId)
                .ipAddress(ipAddress)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        auditLogRepository.save(audit);
        log.info("Recorded Audit Log: [{}] on entity [{}] by user [{}]", action, entityName, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLog> getAuditLogs(Pageable pageable) {
        return PageResponse.from(auditLogRepository.findAll(pageable));
    }
}
