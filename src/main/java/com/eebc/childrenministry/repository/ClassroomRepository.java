package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Classroom;
import com.eebc.childrenministry.entity.ClassroomTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, String> {

    List<Classroom> findByStatus(String status);

    List<Classroom> findByCampusId(String campusId);

    List<Classroom> findByCampusIdAndStatus(String campusId, String status);

    Optional<Classroom> findByCoordinatorId(String coordinatorId);

    Optional<Classroom> findByAssistantId(String assistantId);

    // Find classrooms whose age range covers a child's age in months
    @Query("SELECT c FROM Classroom c WHERE " +
           "c.status = 'ACTIVE' AND " +
           "(:ageMonths >= c.minAgeMonths OR c.minAgeMonths IS NULL) AND " +
           "(:ageMonths <= c.maxAgeMonths OR c.maxAgeMonths IS NULL)")
    List<Classroom> findByAgeInMonths(@Param("ageMonths") int ageMonths);
}



