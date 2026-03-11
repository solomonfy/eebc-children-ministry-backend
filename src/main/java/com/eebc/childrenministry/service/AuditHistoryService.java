package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.AuditHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditHistoryService {

    // History for a specific record — e.g. GET /audit?entity=Room&entityId=abc
    Page<AuditHistory> getByEntityAndId(String entityName, String entityId, Pageable pageable);

    // All changes to an entity type — e.g. GET /audit?entity=Room
    Page<AuditHistory> getByEntity(String entityName, Pageable pageable);

    // Everything a user changed — e.g. GET /audit?changedBy=userId
    Page<AuditHistory> getByUser(String changedBy, Pageable pageable);

    // Date range — e.g. GET /audit?from=2025-01-01&to=2025-01-31
    Page<AuditHistory> getByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Entity + date range combined
    Page<AuditHistory> getByEntityAndDateRange(
            String entityName, LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Count — for badges on detail pages
    long countByEntityAndId(String entityName, String entityId);
}
