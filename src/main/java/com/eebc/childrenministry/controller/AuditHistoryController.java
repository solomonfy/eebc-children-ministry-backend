package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.AuditHistory;
import com.eebc.childrenministry.service.AuditHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditHistoryController {

    private final AuditHistoryService auditService;

    /**
     * Main query endpoint — supports all filter combinations:
     *
     * GET /audit?entity=Room&entityId=abc&page=0&size=25
     * GET /audit?entity=Room&page=0&size=25
     * GET /audit?changedBy=userId&page=0&size=25
     * GET /audit?from=2025-01-01T00:00:00&to=2025-01-31T23:59:59&page=0&size=25
     * GET /audit?entity=Room&from=...&to=...&page=0&size=25
     */
    @GetMapping
    public ResponseEntity<Page<AuditHistory>> query(
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String changedBy,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        // entity + entityId — most common: "show history for this record"
        if (entity != null && entityId != null) {
            return ResponseEntity.ok(
                    auditService.getByEntityAndId(entity, entityId, pageable));
        }

        // entity + date range
        if (entity != null && from != null && to != null) {
            return ResponseEntity.ok(
                    auditService.getByEntityAndDateRange(entity, from, to, pageable));
        }

        // entity only
        if (entity != null) {
            return ResponseEntity.ok(auditService.getByEntity(entity, pageable));
        }

        // user only
        if (changedBy != null) {
            return ResponseEntity.ok(auditService.getByUser(changedBy, pageable));
        }

        // date range only
        if (from != null && to != null) {
            return ResponseEntity.ok(auditService.getByDateRange(from, to, pageable));
        }

        // fallback — return last 25 changes across everything
        return ResponseEntity.ok(
                auditService.getByDateRange(
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now(),
                        pageable));
    }

    /**
     * GET /audit/count?entity=Room&entityId=abc
     * Returns { "count": 7 } — used for the "Show History (7)" button badge
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count(
            @RequestParam String entity,
            @RequestParam String entityId) {
        return ResponseEntity.ok(Map.of(
                "count", auditService.countByEntityAndId(entity, entityId)));
    }
}
