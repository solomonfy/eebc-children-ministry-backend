package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.ServiceRequest;
import com.eebc.childrenministry.dto.ServiceScheduleRequest;
import com.eebc.childrenministry.entity.Service;
import com.eebc.childrenministry.entity.ServiceSchedule;

import java.time.LocalDate;
import java.util.List;

public interface ServiceService {

    // ── Services ──────────────────────────────────────────────────────────
    List<Service> getAllServices();
    List<Service> getUpcomingServices();
    List<Service> getServicesBetween(LocalDate from, LocalDate to);
    List<Service> getServicesByStatus(String status);
    List<Service> getServicesByCampus(String campusId);
    Service getServiceById(String id);
    Service createService(ServiceRequest req);
    Service updateService(String id, ServiceRequest req);
    void deleteService(String id);

    // ── Schedule assignments ──────────────────────────────────────────────
    List<ServiceSchedule> getScheduleByServiceId(String serviceId);
    ServiceSchedule assignTeacher(ServiceScheduleRequest req);
    ServiceSchedule updateAssignment(String id, ServiceScheduleRequest req);
    void removeAssignment(String id);

    // ── Generation (called by cron + seed) ────────────────────────────────
    /**
     * Generates the next Friday (FRIDAY_EVENING) and the following Sunday
     * (SUNDAY_MORNING) as Service records for every campus+ministry combo,
     * skipping any that already exist.
     *
     * @param weeksAhead  how many weeks from today to generate (1 = next occurrence only)
     */
    List<Service> generateWeek(int weeksAhead, String campusId, String ministryId);

    /**
     * Seeds the next {@code weeks} weeks on application startup.
     * Safe to call multiple times — idempotent via existsByServiceDateAndTypeAndCampusId.
     */
    void seedNextWeeks(int weeks, String campusId, String ministryId);

    /**
     * Marks all SCHEDULED or ACTIVE services whose serviceDate < today as COMPLETED.
     * Called by the daily cron job and on startup.
     */
    int markPastServicesCompleted();

    /**
     * Marks all SCHEDULED services whose serviceDate == today as ACTIVE.
     * Called on startup and by the daily cron job.
     */
    int markTodayServicesActive();
}
