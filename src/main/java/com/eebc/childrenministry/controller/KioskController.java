package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ChildDTO;
import com.eebc.childrenministry.entity.Attendance;
import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.entity.Service;
import com.eebc.childrenministry.repository.MinistrySettingRepository;
import com.eebc.childrenministry.repository.RoomRepository;
import com.eebc.childrenministry.repository.ServiceRepository;
import com.eebc.childrenministry.service.AttendanceService;
import com.eebc.childrenministry.service.ChildService;
import com.eebc.childrenministry.service.FamilyService;
import com.eebc.childrenministry.service.GuardianService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * KioskController — dedicated endpoints for the check-in kiosk.
 *
 * All routes are under /kiosk/** which SecurityConfig marks as
 * "authenticated", so any valid JWT (including CHECKIN_OUT_RECEPTIONIST)
 * is accepted without extra role restrictions.
 */
@RestController
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final FamilyService familyService;
    private final ChildService childService;
    private final GuardianService guardianService;
    private final AttendanceService attendanceService;
    private final RoomRepository roomRepository;
    private final ServiceRepository serviceRepository;
    private final MinistrySettingRepository ministrySettingRepository;

    private static final Logger logger = LoggerFactory.getLogger(KioskController.class);

    // ── GET /kiosk/families ────────────────────────────────────────────────────
    // Returns all families for name / phone search on the kiosk.
    @GetMapping("/families")
    public ResponseEntity<List<Family>> getFamilies() {
        logger.info("Kiosk requested family list of size {}", familyService.getAllFamilies().size());
        return ResponseEntity.ok(familyService.getAllFamilies());
    }

    // ── GET /kiosk/children/{familyId} ────────────────────────────────────────
    // Returns ACTIVE children belonging to the given family.
    @GetMapping("/children/{familyId}")
    public ResponseEntity<List<ChildDTO>> getChildren(@PathVariable String familyId) {
        List<ChildDTO> children = childService.getChildrenByFamilyId(familyId)
                .stream()
                .filter(c -> c.getStatus() == null || "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(children);
    }

    // ── GET /kiosk/services ───────────────────────────────────────────────────
    // Returns upcoming / active services (today onward, max 10).
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        LocalDate today = LocalDate.now();
        List<Service> upcoming = serviceRepository.findAll()
                .stream()
                .filter(s -> s.getServiceDate() != null && !s.getServiceDate().isBefore(today))
                .filter(s -> {
                    String status = s.getStatus();
                    return status == null
                            || status.equalsIgnoreCase("SCHEDULED")
                            || status.equalsIgnoreCase("ACTIVE")
                            || status.equalsIgnoreCase("UPCOMING");
                })
                .sorted(Comparator.comparing(Service::getServiceDate))
                .limit(10)
                .collect(Collectors.toList());
        return ResponseEntity.ok(upcoming);
    }

    // ── GET /kiosk/rooms ──────────────────────────────────────────────────────
    // Returns all rooms (used to display assigned room name after check-in).
    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getRooms() {
        return ResponseEntity.ok(roomRepository.findAll());
    }

    // ── GET /kiosk/settings ───────────────────────────────────────────────────
    // Returns the first ministry settings record (check-in feature flags).
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        return ministrySettingRepository.findAll()
                .stream()
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(Map.of()));
    }

    // ── POST /kiosk/pin ───────────────────────────────────────────────────────
    // Looks up a family by guardian PIN for PIN-based check-in.
    @PostMapping("/pin")
    public ResponseEntity<?> findByPin(@RequestBody Map<String, String> body) {
        return guardianService.findByPin(body.get("pin"))
                .map(g -> ResponseEntity.ok((Object) Map.of(
                        "guardianId", g.getId(),
                        "firstName",  g.getFirstName() != null ? g.getFirstName() : "",
                        "lastName",   g.getLastName()  != null ? g.getLastName()  : "",
                        "familyId",   g.getFamilyId()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "PIN not recognized")));
    }

    // ── POST /kiosk/checkin ───────────────────────────────────────────────────
    // Submits one or more attendance records (one per selected child).
    @PostMapping("/checkin")
    public ResponseEntity<List<Attendance>> checkIn(@RequestBody List<CheckinRecord> records) {
        List<Attendance> saved = records.stream()
                .map(r -> {
                    Attendance a = new Attendance();
                    a.setChildId(r.getChildId());
                    a.setFamilyId(r.getFamilyId());
                    a.setServiceId(r.getServiceId());
                    a.setRoomId(r.getRoomId());
                    a.setCheckInTime(r.getCheckInTime() != null ? r.getCheckInTime() : LocalDateTime.now());
                    a.setCheckInMethod(r.getCheckInMethod() != null ? r.getCheckInMethod() : "KIOSK");
                    a.setPickupCode(r.getPickupCode());
                    a.setTagCode(r.getTagCode());
                    a.setStatus(Attendance.AttendanceStatus.PENDING_AT_ROOM);
                    a.setLabelPrinted(false);
                    return attendanceService.createAttendance(a);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(saved);
    }

    // ── Request DTO ───────────────────────────────────────────────────────────
    @Data
    public static class CheckinRecord {
        private String childId;
        private String familyId;
        private String serviceId;
        private String roomId;
        private LocalDateTime checkInTime;
        private String checkInMethod;
        private String pickupCode;
        private String tagCode;
    }
}
