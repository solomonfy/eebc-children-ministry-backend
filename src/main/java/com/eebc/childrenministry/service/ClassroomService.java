package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.*;
import com.eebc.childrenministry.entity.Classroom;
import com.eebc.childrenministry.entity.ClassroomTeacher;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.entity.User;
import com.eebc.childrenministry.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private static final Logger logger = LoggerFactory.getLogger(ClassroomService.class);

    @Autowired private ClassroomRepository classroomRepository;
    @Autowired private ClassroomTeacherRepository classroomTeacherRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private UserRepository userRepository;

    // ── Classroom CRUD ────────────────────────

    public Classroom create(ClassroomRequest req) {
        validateStaffRole(req.coordinatorId(), "CLASS_COORDINATOR");
        validateStaffRole(req.assistantId(),   "CLASS_ASSISTANT");

        Classroom classroom = new Classroom();
        applyRequest(classroom, req);
        logger.info("Creating classroom: {}", req.name());
        return classroomRepository.save(classroom);
    }

    public List<Classroom> getAll() {
        return classroomRepository.findAll();
    }

    public List<Classroom> getActive() {
        return classroomRepository.findByStatus("ACTIVE");
    }

    public Classroom getById(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Classroom not found: " + id));
    }

    public Classroom update(String id, ClassroomRequest req) {
        Classroom classroom = getById(id);
        if (req.coordinatorId() != null) validateStaffRole(req.coordinatorId(), "CLASS_COORDINATOR");
        if (req.assistantId()   != null) validateStaffRole(req.assistantId(),   "CLASS_ASSISTANT");
        applyRequest(classroom, req);
        logger.info("Updated classroom: {}", id);
        return classroomRepository.save(classroom);
    }

    public void delete(String id) {
        Classroom classroom = getById(id);
        classroom.setStatus("INACTIVE");
        classroomRepository.save(classroom);
        logger.info("Deactivated classroom: {}", id);
    }

    // ── Rich Detail Response ──────────────────
    // One call returns classroom + coordinator + assistant
    // + all teachers + all rooms + per-room teacher names
    public ClassroomDetailResponse getDetail(String id) {
        Classroom classroom = getById(id);

        // Coordinator and assistant
        UserSummary coordinator = toUserSummary(classroom.getCoordinatorId());
        UserSummary assistant   = toUserSummary(classroom.getAssistantId());

        // Rooms belonging to this classroom
        List<Room> rooms = roomRepository.findByClassroomId(classroom.getId());

        // All teacher assignments for this classroom
        List<ClassroomTeacher> assignments =
                classroomTeacherRepository.findByClassroomIdAndStatus(classroom.getId(), "ACTIVE");

        // Build a map: roomId → list of teacher names (for room cards)
        Map<String, List<String>> teachersByRoom = assignments.stream()
                .filter(a -> a.getDefaultRoomId() != null)
                .collect(Collectors.groupingBy(
                        ClassroomTeacher::getDefaultRoomId,
                        Collectors.mapping(a -> {
                            User u = userRepository.findById(a.getUserId()).orElse(null);
                            return u != null ? u.getFirstName() + " " + u.getLastName() : "Unknown";
                        }, Collectors.toList())
                ));

        // Build teacher summaries
        List<TeacherSummary> teachers = assignments.stream()
                .map(a -> toTeacherSummary(a, rooms))
                .collect(Collectors.toList());

        // Build room summaries
        List<RoomSummary> roomSummaries = rooms.stream()
                .map(r -> new RoomSummary(
                        r.getId(), r.getName(), r.getCapacity(),
                        r.getMinAgeMonths(), r.getMaxAgeMonths(),
                        r.getRatioChildToVolunteer(), r.getStatus(),
                        teachersByRoom.getOrDefault(r.getId(), List.of())
                ))
                .collect(Collectors.toList());

        int totalCapacity = rooms.stream()
                .mapToInt(r -> r.getCapacity() != null ? Integer.parseInt(r.getCapacity()) : 0)
                .sum();

        return new ClassroomDetailResponse(
                classroom.getId(), classroom.getName(),
                classroom.getDescription(), classroom.getAgeGroup(),
                classroom.getMinAgeMonths(), classroom.getMaxAgeMonths(),
                classroom.getStatus(), classroom.getMaxCapacity(),
                coordinator, assistant,
                teachers, roomSummaries,
                teachers.size(), rooms.size(), totalCapacity
        );
    }

    // ── Teacher Assignment ────────────────────

    public ClassroomTeacher assignTeacher(String classroomId, ClassroomTeacherRequest req) {
        // Classroom must exist
        getById(classroomId);

        // User must exist and have TEACHER role
        validateStaffRole(req.userId(), "TEACHER");

        // Prevent duplicate assignments
        classroomTeacherRepository
                .findByClassroomIdAndUserId(classroomId, req.userId())
                .ifPresent(existing -> {
                    if ("ACTIVE".equals(existing.getStatus())) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Teacher is already assigned to this classroom");
                    }
                    // If previously removed, reactivate instead
                    existing.setStatus("ACTIVE");
                    existing.setDefaultRoomId(req.defaultRoomId());
                    existing.setIsLead(req.isLead() != null ? req.isLead() : false);
                    existing.setAssignedAt(LocalDateTime.now());
                    classroomTeacherRepository.save(existing);
                });

        // Validate default room belongs to this classroom
        if (req.defaultRoomId() != null) {
            Room room = roomRepository.findById(req.defaultRoomId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Room not found"));
            if (!classroomId.equals(room.getClassroomId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Default room does not belong to this classroom");
            }
        }

        ClassroomTeacher assignment = new ClassroomTeacher();
        assignment.setClassroomId(classroomId);
        assignment.setUserId(req.userId());
        assignment.setDefaultRoomId(req.defaultRoomId());
        assignment.setIsLead(req.isLead() != null ? req.isLead() : false);
        assignment.setStatus("ACTIVE");
        assignment.setAssignedAt(LocalDateTime.now());

        logger.info("Assigned teacher {} to classroom {}", req.userId(), classroomId);
        return classroomTeacherRepository.save(assignment);
    }

    public ClassroomTeacher updateTeacherAssignment(String assignmentId, ClassroomTeacherRequest req) {
        ClassroomTeacher assignment = classroomTeacherRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found"));

        if (req.defaultRoomId() != null) assignment.setDefaultRoomId(req.defaultRoomId());
        if (req.isLead()        != null) assignment.setIsLead(req.isLead());

        return classroomTeacherRepository.save(assignment);
    }

    public void removeTeacher(String assignmentId) {
        ClassroomTeacher assignment = classroomTeacherRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found"));
        assignment.setStatus("INACTIVE");
        classroomTeacherRepository.save(assignment);
        logger.info("Removed teacher assignment: {}", assignmentId);
    }

    public List<ClassroomTeacher> getTeachers(String classroomId) {
        return classroomTeacherRepository.findByClassroomIdAndStatus(classroomId, "ACTIVE");
    }

    // ── Auto-match classroom for a child ─────
    // Given a child's age in months, return the right classroom
    public Classroom suggestClassroom(int ageMonths) {
        List<Classroom> matches = classroomRepository.findByAgeInMonths(ageMonths);
        return matches.isEmpty() ? null : matches.get(0);
    }

    // ── Private helpers ───────────────────────

    private void applyRequest(Classroom classroom, ClassroomRequest req) {
        if (req.name()          != null) classroom.setName(req.name());
        if (req.description()   != null) classroom.setDescription(req.description());
        if (req.ageGroup()      != null) classroom.setAgeGroup(req.ageGroup());
        if (req.minAgeMonths()  != null) classroom.setMinAgeMonths(req.minAgeMonths());
        if (req.maxAgeMonths()  != null) classroom.setMaxAgeMonths(req.maxAgeMonths());
        if (req.coordinatorId() != null) classroom.setCoordinatorId(req.coordinatorId());
        if (req.assistantId()   != null) classroom.setAssistantId(req.assistantId());
        if (req.campusId()      != null) classroom.setCampusId(req.campusId());
        if (req.maxCapacity()   != null) classroom.setMaxCapacity(req.maxCapacity());
        if (req.status()        != null) classroom.setStatus(req.status());
    }

    // Soft-validates that a userId exists and has the expected role
    // Logs a warning but does not hard-fail (role may be updated separately)
    private void validateStaffRole(String userId, String expectedRole) {
        if (userId == null) return;
        userRepository.findById(userId).ifPresentOrElse(user -> {
            if (!expectedRole.equalsIgnoreCase(user.getRole())) {
                logger.warn("User {} has role '{}' but expected '{}'",
                        userId, user.getRole(), expectedRole);
            }
        }, () -> {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found: " + userId);
        });
    }

    private UserSummary toUserSummary(String userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .map(u -> new UserSummary(
                        u.getId(), u.getFirstName(), u.getLastName(),
                        u.getEmail(), u.getPhone(), u.getRole(), u.getPhotoUrl()))
                .orElse(null);
    }

    private TeacherSummary toTeacherSummary(ClassroomTeacher a, List<Room> classroomRooms) {
        User u = userRepository.findById(a.getUserId()).orElse(null);
        String roomName = classroomRooms.stream()
                .filter(r -> r.getId().equals(a.getDefaultRoomId()))
                .map(Room::getName)
                .findFirst().orElse(null);

        return new TeacherSummary(
                a.getId(),
                a.getUserId(),
                u != null ? u.getFirstName() : "",
                u != null ? u.getLastName()  : "",
                u != null ? u.getEmail()       : "",
                u != null ? u.getPhone()       : "",
                u != null ? u.getPhotoUrl()  : null,
                Boolean.TRUE.equals(a.getIsLead()),
                a.getDefaultRoomId(),
                roomName,
                a.getStatus()
        );
    }
}
