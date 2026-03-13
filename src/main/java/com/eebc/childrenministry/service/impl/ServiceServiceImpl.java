package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.dto.ServiceRequest;
import com.eebc.childrenministry.dto.ServiceScheduleRequest;
import com.eebc.childrenministry.entity.Service;
import com.eebc.childrenministry.entity.ServiceSchedule;
import com.eebc.childrenministry.repository.ServiceRepository;
import com.eebc.childrenministry.repository.ServiceScheduleRepository;
import com.eebc.childrenministry.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    // ── Service times (CST) ───────────────────────────────────────────────
    private static final LocalTime FRIDAY_START  = LocalTime.of(19, 0);   // 7:00 PM
    private static final LocalTime FRIDAY_END    = LocalTime.of(21, 30);  // 9:30 PM
    private static final LocalTime SUNDAY_START  = LocalTime.of(10, 0);   // 10:00 AM
    private static final LocalTime SUNDAY_END    = LocalTime.of(12, 30);  // 12:30 PM

    private final ServiceRepository          serviceRepo;
    private final ServiceScheduleRepository  scheduleRepo;

    // ── Services ──────────────────────────────────────────────────────────

    @Override
    public List<Service> getAllServices() {
        try {
            List<Service> services = serviceRepo.findAllByOrderByServiceDateAsc();
            logger.info("Fetched all services, count: {}", services.size());
            return services;
        } catch (Exception e) {
            logger.error("Error fetching all services: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Service> getUpcomingServices() {
        try {
            List<Service> services = serviceRepo
                    .findByServiceDateGreaterThanEqualOrderByServiceDateAsc(LocalDate.now());
            logger.info("Fetched upcoming services, count: {}", services.size());
            return services;
        } catch (Exception e) {
            logger.error("Error fetching upcoming services: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Service> getServicesBetween(LocalDate from, LocalDate to) {
        try {
            return serviceRepo.findByServiceDateBetweenOrderByServiceDateAsc(from, to);
        } catch (Exception e) {
            logger.error("Error fetching services between {} and {}: {}", from, to, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Service> getServicesByStatus(String status) {
        return serviceRepo.findByStatus(status);
    }

    @Override
    public List<Service> getServicesByCampus(String campusId) {
        return serviceRepo.findByCampusId(campusId);
    }

    @Override
    public Service getServiceById(String id) {
        try {
            Service service = serviceRepo.findById(id).orElse(null);
            logger.info("Fetched service {}: {}", id, service != null ? "found" : "not found");
            return service;
        } catch (Exception e) {
            logger.error("Error fetching service {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Service createService(ServiceRequest req) {
        try {
            validate(req);
            if (serviceRepo.existsByServiceDateAndTypeAndCampusId(
                    req.serviceDate(), req.type(), req.campusId())) {
                throw new IllegalStateException(
                        "A " + req.type() + " service on " + req.serviceDate()
                        + " already exists for this campus.");
            }
            Service service = build(req);
            Service saved = serviceRepo.save(service);
            logger.info("Created service: id={}, date={}, type={}", saved.getId(),
                    saved.getServiceDate(), saved.getType());
            return saved;
        } catch (Exception e) {
            logger.error("Error creating service: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Service updateService(String id, ServiceRequest req) {
        try {
            Service service = serviceRepo.findById(id).orElse(null);
            if (service == null) {
                logger.warn("Service {} not found for update", id);
                return null;
            }
            if (req.name()        != null) service.setName(req.name());
            if (req.serviceDate() != null) service.setServiceDate(req.serviceDate());
            if (req.startTime()   != null) service.setStartTime(req.startTime());
            if (req.endTime()     != null) service.setEndTime(req.endTime());
            if (req.type()        != null) service.setType(req.type());
            if (req.status()      != null) service.setStatus(req.status());
            if (req.campusId()    != null) service.setCampusId(req.campusId());
            if (req.ministryId()  != null) service.setMinistryId(req.ministryId());
            Service updated = serviceRepo.save(service);
            logger.info("Updated service {}", id);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating service {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteService(String id) {
        try {
            if (!serviceRepo.existsById(id)) {
                logger.warn("Service {} not found for deletion", id);
                return;
            }
            scheduleRepo.deleteByServiceId(id);   // remove assignments first
            serviceRepo.deleteById(id);
            logger.info("Deleted service {} and its schedule assignments", id);
        } catch (Exception e) {
            logger.error("Error deleting service {}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ── Schedule assignments ──────────────────────────────────────────────

    @Override
    public List<ServiceSchedule> getScheduleByServiceId(String serviceId) {
        return scheduleRepo.findByServiceId(serviceId);
    }

    @Override
    public ServiceSchedule assignTeacher(ServiceScheduleRequest req) {
        try {
            if (scheduleRepo.existsByServiceIdAndClassroomIdAndTeacherId(
                    req.serviceId(), req.classroomId(), req.teacherId())) {
                throw new IllegalStateException("Teacher is already assigned to this classroom for this service.");
            }
            // Conflict guard: one teacher cannot be in two classrooms in the same service
            if (scheduleRepo.existsByServiceIdAndTeacherId(req.serviceId(), req.teacherId())) {
                throw new IllegalStateException("Teacher is already assigned to a different classroom in this service.");
            }
            ServiceSchedule assignment = new ServiceSchedule();
            assignment.setServiceId(req.serviceId());
            assignment.setClassroomId(req.classroomId());
            assignment.setRoomId(req.roomId());
            assignment.setTeacherId(req.teacherId());
            assignment.setRole(req.role() != null ? req.role() : "ASSISTANT");
            assignment.setStatus(req.status() != null ? req.status() : "SCHEDULED");
            ServiceSchedule saved = scheduleRepo.save(assignment);
            logger.info("Assigned teacher {} to classroom {} for service {}",
                    req.teacherId(), req.classroomId(), req.serviceId());
            return saved;
        } catch (Exception e) {
            logger.error("Error assigning teacher: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ServiceSchedule updateAssignment(String id, ServiceScheduleRequest req) {
        try {
            ServiceSchedule a = scheduleRepo.findById(id).orElse(null);
            if (a == null) return null;
            if (req.roomId()    != null) a.setRoomId(req.roomId());
            if (req.role()      != null) a.setRole(req.role());
            if (req.status()    != null) a.setStatus(req.status());
            ServiceSchedule updated = scheduleRepo.save(a);
            logger.info("Updated assignment {}", id);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating assignment {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void removeAssignment(String id) {
        try {
            if (scheduleRepo.existsById(id)) {
                scheduleRepo.deleteById(id);
                logger.info("Removed assignment {}", id);
            } else {
                logger.warn("Assignment {} not found for removal", id);
            }
        } catch (Exception e) {
            logger.error("Error removing assignment {}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ── Generation ────────────────────────────────────────────────────────

    @Override
    public List<Service> generateWeek(int weeksAhead, String campusId, String ministryId) {
        List<Service> created = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int w = 0; w < weeksAhead; w++) {
            LocalDate friday = today
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                    .plusWeeks(w);
            LocalDate sunday = friday.plusDays(2); // always the Sunday after that Friday

            created.addAll(generateSingleService(
                    friday, "FRIDAY_EVENING", FRIDAY_START, FRIDAY_END,
                    "Friday Evening Service", campusId, ministryId));

            created.addAll(generateSingleService(
                    sunday, "SUNDAY_MORNING", SUNDAY_START, SUNDAY_END,
                    "Sunday Morning Service", campusId, ministryId));
        }

        logger.info("Generated {} new service(s) for {} week(s) ahead", created.size(), weeksAhead);
        return created;
    }

    @Override
    public void seedNextWeeks(int weeks, String campusId, String ministryId) {
        logger.info("Seeding next {} weeks of services for campus={} ministry={}",
                weeks, campusId, ministryId);
        generateWeek(weeks, campusId, ministryId);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private List<Service> generateSingleService(
            LocalDate date, String type,
            LocalTime start, LocalTime end,
            String namePrefix,
            String campusId, String ministryId) {

        List<Service> created = new ArrayList<>();
        if (serviceRepo.existsByServiceDateAndTypeAndCampusId(date, type, campusId)) {
            logger.debug("Service {} on {} already exists for campus {}, skipping", type, date, campusId);
            return created;
        }
        Service s = new Service();
        s.setName(namePrefix);
        s.setServiceDate(date);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setType(type);
        s.setStatus("SCHEDULED");
        s.setCampusId(campusId);
        s.setMinistryId(ministryId);
        created.add(serviceRepo.save(s));
        logger.info("Generated {} service on {}", type, date);
        return created;
    }

    private void validate(ServiceRequest req) {
        if (req.serviceDate() == null)
            throw new IllegalArgumentException("serviceDate is required");
        if (req.type() == null || req.type().isBlank())
            throw new IllegalArgumentException("type is required (FRIDAY_EVENING or SUNDAY_MORNING)");
        if (req.campusId() == null || req.campusId().isBlank())
            throw new IllegalArgumentException("campusId is required");
        if (req.ministryId() == null || req.ministryId().isBlank())
            throw new IllegalArgumentException("ministryId is required");
    }

    private Service build(ServiceRequest req) {
        Service s = new Service();
        s.setName(req.name() != null ? req.name() : defaultName(req.type()));
        s.setServiceDate(req.serviceDate());
        s.setStartTime(req.startTime()  != null ? req.startTime()  : defaultStart(req.type()));
        s.setEndTime(req.endTime()      != null ? req.endTime()    : defaultEnd(req.type()));
        s.setType(req.type());
        s.setStatus(req.status()        != null ? req.status()     : "SCHEDULED");
        s.setCampusId(req.campusId());
        s.setMinistryId(req.ministryId());
        return s;
    }

    private String defaultName(String type) {
        return "FRIDAY_EVENING".equals(type) ? "Friday Evening Service" : "Sunday Morning Service";
    }

    private LocalTime defaultStart(String type) {
        return "FRIDAY_EVENING".equals(type) ? FRIDAY_START : SUNDAY_START;
    }

    private LocalTime defaultEnd(String type) {
        return "FRIDAY_EVENING".equals(type) ? FRIDAY_END : SUNDAY_END;
    }
}
