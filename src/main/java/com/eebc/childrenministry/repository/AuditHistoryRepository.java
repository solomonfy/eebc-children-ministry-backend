package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.AuditHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditHistoryRepository extends JpaRepository<AuditHistory, String> {

    // History for a specific record (e.g. all changes to Room with id=X)
    Page<AuditHistory> findByEntityNameAndEntityIdOrderByChangedAtDesc(
            String entityName, String entityId, Pageable pageable);

    // All changes to an entity type (e.g. all Room changes)
    Page<AuditHistory> findByEntityNameOrderByChangedAtDesc(
            String entityName, Pageable pageable);

    // Everything a specific user changed
    Page<AuditHistory> findByChangedByOrderByChangedAtDesc(
            String changedBy, Pageable pageable);

    // Date range query
    Page<AuditHistory> findByChangedAtBetweenOrderByChangedAtDesc(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Entity + date range
    Page<AuditHistory> findByEntityNameAndChangedAtBetweenOrderByChangedAtDesc(
            String entityName, LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Quick count — useful for badges
    long countByEntityNameAndEntityId(String entityName, String entityId);
}
