package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Attendance;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.AttendanceRepository;
import com.eebc.childrenministry.repository.ChildRepository;
import com.eebc.childrenministry.repository.RoomRepository;
import com.eebc.childrenministry.service.AttendanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    ChildRepository childRepository;

    @Autowired
    RoomRepository roomRepository;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Override
    public List<Attendance> getAllAttendances() {
        List<Attendance> attendances = new ArrayList<>();
        try {
            attendances = attendanceRepository.findAll();
            logger.info("Retrieved {} attendances from the repository.", attendances.size());
            return attendances;
        } catch (Exception e) {
            logger.error("Error fetching attendances: {}", e.getMessage());
            System.err.println("Error fetching attendances: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Attendance createAttendance(Attendance attendance) {
        if (attendance.getRoomId() == null) {
            attendance.setRoomId(resolveRoomForChild(attendance.getChildId()));
        }

        if(checkIfChildIsCheckedIn(attendance.getChildId(), attendance.getServiceId())){
            throw new IllegalStateException("Attendance already exists for child ID: " + attendance.getChildId() + " and service ID: " + attendance.getServiceId());
        }

        if(attendance.getChildId() == null){
            throw new IllegalArgumentException("Child ID must be provided for attendance.");
        }
        return attendanceRepository.save(attendance);
    }

    private String resolveRoomForChild(String childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found: " + childId));

        int ageInMonths = calculateAgeInMonths(child.getBirthDate());

        return roomRepository.findAll().stream()
                .filter(room -> room.getMinAgeMonths() != null && room.getMaxAgeMonths() != null)
                .filter(room -> ageInMonths >= room.getMinAgeMonths() && ageInMonths <= room.getMaxAgeMonths())
                .findFirst()
                .map(Room::getId)
                .orElseThrow(() -> new IllegalStateException("No matching room found for child age: " + ageInMonths + " months"));
    }

    private int calculateAgeInMonths(LocalDate dateOfBirth) {
        return (int) ChronoUnit.MONTHS.between(dateOfBirth, LocalDate.now());
    }

    private boolean checkIfChildIsCheckedIn(String childId, String serviceId) {
        Attendance attendance = attendanceRepository.findByChildIdAndServiceId(childId, serviceId);
        System.out.println("Checking if child ID " + childId + " is already checked in for service ID " + serviceId + ": " + (attendance != null));
        if(attendance != null){
            return true;
        }
        return false;
    }
}