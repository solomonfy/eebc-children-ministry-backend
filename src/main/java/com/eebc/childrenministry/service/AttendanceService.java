package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Attendance;

import java.util.List;

public interface AttendanceService {
    List<Attendance> getAllAttendances();
    Attendance createAttendance(Attendance attendance);
}
