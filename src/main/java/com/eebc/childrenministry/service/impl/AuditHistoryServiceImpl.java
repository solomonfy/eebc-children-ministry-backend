package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.AuditHistory;
import com.eebc.childrenministry.repository.AuditHistoryRepository;
import com.eebc.childrenministry.service.AuditHistoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditHistoryServiceImpl implements AuditHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(AuditHistoryServiceImpl.class);

    private final AuditHistoryRepository auditRepo;

    @Override
    public Page<AuditHistory> getByEntityAndId(
            String entityName, String entityId, Pageable pageable) {
        try {
            return auditRepo.findByEntityNameAndEntityIdOrderByChangedAtDesc(
                    entityName, entityId, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit for {}/{}: {}", entityName, entityId, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AuditHistory> getByEntity(String entityName, Pageable pageable) {
        try {
            return auditRepo.findByEntityNameOrderByChangedAtDesc(entityName, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit for entity {}: {}", entityName, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AuditHistory> getByUser(String changedBy, Pageable pageable) {
        try {
            return auditRepo.findByChangedByOrderByChangedAtDesc(changedBy, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit for user {}: {}", changedBy, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AuditHistory> getByDateRange(
            LocalDateTime from, LocalDateTime to, Pageable pageable) {
        try {
            return auditRepo.findByChangedAtBetweenOrderByChangedAtDesc(from, to, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit by date range: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AuditHistory> getByEntityAndDateRange(
            String entityName, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        try {
            return auditRepo.findByEntityNameAndChangedAtBetweenOrderByChangedAtDesc(
                    entityName, from, to, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit for entity {} by date range: {}",
                    entityName, e.getMessage());
            throw e;
        }
    }

    @Override
    public long countByEntityAndId(String entityName, String entityId) {
        return auditRepo.countByEntityNameAndEntityId(entityName, entityId);
    }
}
