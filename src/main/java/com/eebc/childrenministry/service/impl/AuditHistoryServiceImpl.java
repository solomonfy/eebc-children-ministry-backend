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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // Delegates to getByEntityAndId — satisfies interface overload
    @Override
    public Page<AuditHistory> getByEntity(String entityName, String entityId, Pageable pageable) {
        return getByEntityAndId(entityName, entityId, pageable);
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

    @Override
    public Page<AuditHistory> getByEntityIds(
            String entityName, List<String> entityIds, Pageable pageable) {
        try {
            return auditRepo.findByEntityNameAndEntityIdInOrderByChangedAtDesc(
                    entityName, entityIds, pageable);
        } catch (Exception e) {
            logger.error("Error fetching audit for {} ids - {}", entityName, e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, List<AuditHistory>> getGroupedByEntityIds(
            String entityName, List<String> entityIds) {
        try {
            List<AuditHistory> all = auditRepo
                    .findByEntityNameAndEntityIdInOrderByChangedAtDesc(entityName, entityIds);

            // Every requested ID gets a key even if it has zero records
            Map<String, List<AuditHistory>> result = new LinkedHashMap<>();
            entityIds.forEach(id -> result.put(id, new ArrayList<>()));
            all.forEach(r -> result.get(r.getEntityId()).add(r));

            logger.info("Grouped audit records for {} - {} ids, {} total records",
                    entityName, entityIds.size(), all.size());
            return result;
        } catch (Exception e) {
            logger.error("Error grouping audit for {} - {}", entityName, e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, Long> countGroupedByEntityIds(
            String entityName, List<String> entityIds) {
        try {
            List<AuditHistory> all = auditRepo
                    .findByEntityNameAndEntityIdInOrderByChangedAtDesc(entityName, entityIds);

            // Seed every requested id with 0 so missing ones still appear in response
            Map<String, Long> result = new LinkedHashMap<>();
            entityIds.forEach(id -> result.put(id, 0L));
            all.forEach(r -> result.merge(r.getEntityId(), 1L, Long::sum));

            logger.info("Counted audit records for {} - {} ids, {} total",
                    entityName, entityIds.size(), all.size());
            return result;
        } catch (Exception e) {
            logger.error("Error counting grouped audit for {} - {}", entityName, e.getMessage());
            throw e;
        }
    }
}