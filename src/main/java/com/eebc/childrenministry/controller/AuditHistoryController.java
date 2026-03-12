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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditHistoryController {

    private final AuditHistoryService auditService;

    /**
     * Main query endpoint — supports all filter combinations:
     *
     * Single:   GET /audit?entity=Room&entityId=abc&page=0&size=25
     * Multiple: GET /audit?entity=Room&entityId=id1,id2,id3&page=0&size=25
     * Entity:   GET /audit?entity=Room&page=0&size=25
     * User:     GET /audit?changedBy=userId&page=0&size=25
     * Range:    GET /audit?from=2025-01-01T00:00:00&to=2025-01-31T23:59:59
     */
    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String changedBy,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "25")  int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        // ── entity + entityId ──────────────────────────────────────────────
        if (entity != null && entityId != null) {
            List<String> ids = parseIds(entityId);

            if (ids.size() == 1) {
                // Single ID — flat paged response
                Page<AuditHistory> result = auditService.getByEntityAndId(entity, ids.get(0), pageable);
                return ResponseEntity.ok(Map.of(
                        "records",    result.getContent(),
                        "total",      result.getTotalElements(),
                        "totalPages", result.getTotalPages(),
                        "page",       page
                ));
            }

            // Multiple IDs — flat paged + grouped + per-id counts in one response
            Page<AuditHistory> paged            = auditService.getByEntityIds(entity, ids, pageable);
            Map<String, List<AuditHistory>> grouped = auditService.getGroupedByEntityIds(entity, ids);
            Map<String, Long> counts            = grouped.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

            return ResponseEntity.ok(Map.of(
                    "records",    paged.getContent(),   // flat newest-first across all ids
                    "grouped",    grouped,               // { "id1": [...], "id2": [...] }
                    "counts",     counts,                // { "id1": 3, "id2": 0 }
                    "total",      paged.getTotalElements(),
                    "totalPages", paged.getTotalPages(),
                    "page",       page
            ));
        }

        // ── entity + date range ────────────────────────────────────────────
        if (entity != null && from != null && to != null) {
            return ResponseEntity.ok(
                    auditService.getByEntityAndDateRange(entity, from, to, pageable));
        }

        // ── entity only ────────────────────────────────────────────────────
        if (entity != null) {
            return ResponseEntity.ok(auditService.getByEntity(entity, pageable));
        }

        // ── changedBy only ─────────────────────────────────────────────────
        if (changedBy != null) {
            return ResponseEntity.ok(auditService.getByUser(changedBy, pageable));
        }

        // ── date range only ────────────────────────────────────────────────
        if (from != null && to != null) {
            return ResponseEntity.ok(auditService.getByDateRange(from, to, pageable));
        }

        // ── fallback — last 30 days ────────────────────────────────────────
        return ResponseEntity.ok(
                auditService.getByDateRange(
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now(),
                        pageable));
    }

    /**
     * GET /audit/count?entity=Room&entityId=abc
     * GET /audit/count?entity=Classroom&entityId=id1,id2,id3
     *
     * Single   → { "count": 7 }
     * Multiple → { "total": 15, "counts": { "id1": 8, "id2": 7 } }
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> count(
            @RequestParam String entity,
            @RequestParam String entityId) {

        List<String> ids = parseIds(entityId);

        if (ids.size() == 1) {
            long count = auditService.countByEntityAndId(entity, ids.get(0));
            return ResponseEntity.ok(Map.of("count", count));
        }

        Map<String, Long> counts = auditService.countGroupedByEntityIds(entity, ids);
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        return ResponseEntity.ok(Map.of(
                "total",  total,
                "counts", counts
        ));
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    // Splits "id1,id2,id3" or a single "id1" into a clean List<String>
    private List<String> parseIds(String entityId) {
        return Arrays.stream(entityId.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}