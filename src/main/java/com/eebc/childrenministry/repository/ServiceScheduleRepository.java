package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ServiceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceScheduleRepository extends JpaRepository<ServiceSchedule, String> {

    List<ServiceSchedule> findByServiceId(String serviceId);

    List<ServiceSchedule> findByTeacherId(String teacherId);

    List<ServiceSchedule> findByClassroomId(String classroomId);

    List<ServiceSchedule> findByServiceIdAndClassroomId(String serviceId, String classroomId);

    boolean existsByServiceIdAndClassroomIdAndTeacherId(
            String serviceId, String classroomId, String teacherId);

    // Conflict guard: is this teacher already assigned to any slot in this service?
    boolean existsByServiceIdAndTeacherId(String serviceId, String teacherId);

    void deleteByServiceId(String serviceId);
}
