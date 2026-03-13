package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.NotificationLog;
import com.eebc.childrenministry.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /notifications?serviceId=&page=0&size=25
     * Returns paginated notification logs.
     * Pass serviceId to filter by a specific service, omit for all logs.
     */
    @GetMapping
    public ResponseEntity<Page<NotificationLog>> list(
            @RequestParam(required = false) String serviceId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(serviceId, page, size));
    }

    /**
     * POST /notifications/remind/{serviceId}
     * Manually sends reminder emails to all teachers assigned to the service.
     */
    @PostMapping("/remind/{serviceId}")
    public ResponseEntity<Map<String, String>> remind(@PathVariable String serviceId) {
        notificationService.sendManualReminder(serviceId);
        return ResponseEntity.ok(Map.of("message", "Reminders sent for service " + serviceId));
    }
}
