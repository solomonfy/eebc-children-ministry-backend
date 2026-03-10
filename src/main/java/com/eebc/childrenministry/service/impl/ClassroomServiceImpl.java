package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.dto.*;
import com.eebc.childrenministry.entity.Classroom;
import com.eebc.childrenministry.entity.ClassroomTeacher;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.entity.User;
import com.eebc.childrenministry.repository.*;
import com.eebc.childrenministry.service.ClassroomService;
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
public class ClassroomServiceImpl implements ClassroomService {

    private static final Logger logger = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    @Autowired private ClassroomRepository        classroomRepository;
    @Autowired private ClassroomTeacherRepository classroomTeacherRepository;
    @Autowired private RoomRepository             roomRepository;
    @Autowired private UserRepository             userRepository;

    // ── Classroom CRUD ────────────────────────

    @Override
    public Classroom create(ClassroomRequest req) {
        validateStaffRole(req.coordinatorId(), "CLASS_COORDINATOR");
        validateStaffRole(req.assistantId(),   "CLASS_ASSISTANT");
        Classroom classroom = new Classroom();
        applyRequest(classroom, req);
        logger.info("Creating classroom: {}", req.name());
        return classroomRepository.save(classroom);
    }

    @Override
    public List<Classroom> getAll() {
        return classroomRepository.findAll();
    }

    @Override
    public List<Classroom> getActive() {
        return classroomRepository.findByStatus("ACTIVE");
    }

    @Override
    public Classroom getById(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Classroom not found: " + id));
    }

    @Override
    public Classroom update(String id, ClassroomRequest req) {
        Classroom classroom = getById(id);
        if (req.coordinatorId() != null) validateStaffRole(req.coordinatorId(), "CLASS_COORDINATOR");
        if (req.assistantId()   != null) validateStaffRole(req.assistantId(),   "CLASS_ASSISTANT");
        applyRequest(classroom, req);
        logger.info("Updated classroom: {}", id);
        return classroomRepository.save(classroom);
    }

    @Override
    public void delete(String id) {
        Classroom classroom = getById(id);
        classroom.setStatus("INACTIVE");
        classroomRepository.save(classroom);
        logger.info("Deactivated classroom: {}", id);
    }

    // ── Rich Detail Response ──────────────────

    @Override
    public ClassroomDetailResponse getDetail(String id) {
        Classroom classroom = getById(id);

        UserSummary coordinator = toUserSummary(classroom.getCoordinatorId());
        UserSummary assistant   = toUserSummary(classroom.getAssistantId());

        List<Room> rooms = roomRepository.findByClassroomId(classroom.getId());

        List<ClassroomTeacher> assignments =
                classroomTeacherRepository.findByClassroomIdAndStatus(classroom.getId(), "ACTIVE");

        // roomId → teacher names (used in room cards on the UI)
        Map<String, List<String>> teachersByRoom = assignments.stream()
                .filter(a -> a.getDefaultRoomId() != null)
                .collect(Collectors.groupingBy(
                        ClassroomTeacher::getDefaultRoomId,
                        Collectors.mapping(a -> {
                            User u = userRepository.findById(a.getUserId()).orElse(null);
                            return u != null ? u.getFirstName() + " " + u.getLastName() : "Unknown";
                        }, Collectors.toList())
                ));

        List<TeacherSummaryDTO> teachers = assignments.stream()
                .map(a -> toTeacherSummary(a, rooms))
                .collect(Collectors.toList());

        List<RoomSummaryDTO> roomSummaries = rooms.stream()
                .map(r -> new RoomSummaryDTO(
                        r.getId(), r.getName(), r.getCapacity(),
                        r.getMinAgeMonths(), r.getMaxAgeMonths(),
                        r.getRatioChildToVolunteer(), r.getStatus(),
                        teachersByRoom.getOrDefault(r.getId(), List.of())
                ))
                .collect(Collectors.toList());

        int totalCapacity = rooms.stream()
                .mapToInt(r -> r.getCapacity() != null ? r.getCapacity() : 0)
                .sum();

        logger.info("Fetched classroom detail for id: {}", id);
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

    // ── Teacher Assignments ───────────────────

    @Override
    public ClassroomTeacher assignTeacher(String classroomId, ClassroomTeacherRequest req) {
        getById(classroomId);
        validateStaffRole(req.userId(), "TEACHER");

        // If previously removed, reactivate instead of creating a duplicate row
        var existing = classroomTeacherRepository
                .findByClassroomIdAndUserId(classroomId, req.userId());

        if (existing.isPresent()) {
            ClassroomTeacher ct = existing.get();
            if ("ACTIVE".equals(ct.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Teacher is already assigned to this classroom");
            }
            ct.setStatus("ACTIVE");
            ct.setDefaultRoomId(req.defaultRoomId());
            ct.setIsLead(req.isLead() != null ? req.isLead() : false);
            ct.setAssignedAt(LocalDateTime.now());
            return classroomTeacherRepository.save(ct);
        }

        validateRoomBelongsToClassroom(classroomId, req.defaultRoomId());

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

    @Override
    public ClassroomTeacher updateTeacherAssignment(String assignmentId, ClassroomTeacherRequest req) {
        ClassroomTeacher assignment = classroomTeacherRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found: " + assignmentId));

        if (req.defaultRoomId() != null) {
            validateRoomBelongsToClassroom(assignment.getClassroomId(), req.defaultRoomId());
            assignment.setDefaultRoomId(req.defaultRoomId());
        }
        if (req.isLead() != null) assignment.setIsLead(req.isLead());

        return classroomTeacherRepository.save(assignment);
    }

    @Override
    public void removeTeacher(String assignmentId) {
        ClassroomTeacher assignment = classroomTeacherRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found: " + assignmentId));
        assignment.setStatus("INACTIVE");
        classroomTeacherRepository.save(assignment);
        logger.info("Removed teacher assignment: {}", assignmentId);
    }

    // ── GET /classrooms/{id}/teachers ─────────────────────────────────────────
    // The key difference from the old implementation:
    //   OLD → returned List<ClassroomTeacher>  (raw join table rows, no user fields)
    //   NEW → returns List<TeacherSummaryDTO>  (joined with users table)
    //
    // How firstName / lastName / photoUrl get to the UI:
    //   1. Fetch all ACTIVE assignments for this classroom from classroom_teachers
    //   2. For each assignment, call userRepository.findById(userId)
    //   3. Map into TeacherSummaryDTO which includes those user fields
    //   4. Controller returns the DTO list as JSON — UI gets everything in one call
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<TeacherSummaryDTO> getTeachers(String classroomId) {
        getById(classroomId); // validates classroom exists, throws 404 if not

        List<ClassroomTeacher> assignments =
                classroomTeacherRepository.findByClassroomIdAndStatus(classroomId, "ACTIVE");

        // Pre-fetch rooms once so toTeacherSummary doesn't query per assignment
        List<Room> classroomRooms = roomRepository.findByClassroomId(classroomId);

        return assignments.stream()
                .map(a -> toTeacherSummary(a, classroomRooms))
                .collect(Collectors.toList());
    }

    // ── Auto-suggest ──────────────────────────

    @Override
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

    private void validateStaffRole(String userId, String expectedRole) {
        if (userId == null) return;
        userRepository.findById(userId).ifPresentOrElse(
                user -> {
                    if (!expectedRole.equalsIgnoreCase(user.getRole())) {
                        logger.warn("User {} has role '{}' but expected '{}'",
                                userId, user.getRole(), expectedRole);
                    }
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + userId); }
        );
    }

    private void validateRoomBelongsToClassroom(String classroomId, String roomId) {
        if (roomId == null) return;
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room not found: " + roomId));
        if (!classroomId.equals(room.getClassroomId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Room " + roomId + " does not belong to classroom " + classroomId);
        }
    }

    // Joins classroom_teachers row with users + rooms to build the enriched DTO
    private TeacherSummaryDTO toTeacherSummary(ClassroomTeacher a, List<Room> classroomRooms) {
        User u = userRepository.findById(a.getUserId()).orElse(null);

        String roomName = classroomRooms.stream()
                .filter(r -> r.getId().equals(a.getDefaultRoomId()))
                .map(Room::getName)
                .findFirst()
                .orElse(null);

        return new TeacherSummaryDTO(
                a.getId(),                              // assignmentId
                a.getUserId(),                          // userId
                u != null ? u.getFirstName()  : "",     // firstName  ← from users
                u != null ? u.getLastName()   : "",     // lastName   ← from users
                u != null ? u.getEmail()      : "",     // email      ← from users
                u != null ? u.getPhone()      : "",     // phone      ← from users
                u != null ? u.getPhotoUrl()   : null,   // photoUrl   ← from users
                Boolean.TRUE.equals(a.getIsLead()),     // isLead
                a.getDefaultRoomId(),                   // defaultRoomId
                roomName,                               // defaultRoomName ← from rooms
                a.getStatus()                           // status
        );
    }

    private UserSummary toUserSummary(String userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .map(u -> new UserSummary(
                        u.getId(), u.getFirstName(), u.getLastName(),
                        u.getEmail(), u.getPhone(), u.getRole(), u.getPhotoUrl()))
                .orElse(null);
    }
}