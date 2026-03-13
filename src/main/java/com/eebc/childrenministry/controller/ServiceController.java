package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ServiceRequest;
import com.eebc.childrenministry.dto.ServiceScheduleRequest;
import com.eebc.childrenministry.entity.Service;
import com.eebc.childrenministry.entity.ServiceSchedule;
import com.eebc.childrenministry.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    // ── Services ──────────────────────────────────────────────────────────

    /**
     * GET /services                           → all services (sorted by date)
     * GET /services?upcoming=true             → from today onwards
     * GET /services?status=SCHEDULED          → by status
     * GET /services?campusId=X                → by campus
     * GET /services?from=2025-01-01&to=...    → date range
     */
    @GetMapping
    public ResponseEntity<List<Service>> list(
            @RequestParam(required = false) Boolean upcoming,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String campusId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (Boolean.TRUE.equals(upcoming)) {
            return ResponseEntity.ok(serviceService.getUpcomingServices());
        }
        if (from != null && to != null) {
            return ResponseEntity.ok(serviceService.getServicesBetween(from, to));
        }
        if (status != null) {
            return ResponseEntity.ok(serviceService.getServicesByStatus(status));
        }
        if (campusId != null) {
            return ResponseEntity.ok(serviceService.getServicesByCampus(campusId));
        }
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> get(@PathVariable String id) {
        Service service = serviceService.getServiceById(id);
        return service != null
                ? ResponseEntity.ok(service)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(serviceService.createService(req));
        } catch (IllegalStateException e) {
            // Duplicate service — return 409 so the frontend can show the message
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> update(
            @PathVariable String id, @RequestBody ServiceRequest req) {
        Service updated = serviceService.updateService(id, req);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // ── Schedule assignments ──────────────────────────────────────────────

    /** GET /services/{id}/schedule → all assignments for this service */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<ServiceSchedule>> getSchedule(@PathVariable String id) {
        return ResponseEntity.ok(serviceService.getScheduleByServiceId(id));
    }

    /** POST /services/schedule → assign a teacher to a classroom for a service */
    @PostMapping("/schedule")
    public ResponseEntity<ServiceSchedule> assign(@RequestBody ServiceScheduleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceService.assignTeacher(req));
    }

    /** PUT /services/schedule/{id} → update role/status/room of an existing assignment */
    @PutMapping("/schedule/{id}")
    public ResponseEntity<ServiceSchedule> updateAssignment(
            @PathVariable String id, @RequestBody ServiceScheduleRequest req) {
        ServiceSchedule updated = serviceService.updateAssignment(id, req);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    /** DELETE /services/schedule/{id} → remove an assignment */
    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<Void> removeAssignment(@PathVariable String id) {
        serviceService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
