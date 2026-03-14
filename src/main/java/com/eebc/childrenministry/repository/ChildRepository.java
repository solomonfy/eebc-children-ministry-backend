package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, String> {

    @Query("SELECT DISTINCT c FROM Child c LEFT JOIN FETCH c.allergies")
    List<Child> findAllWithAllergies();

    @Query("SELECT DISTINCT c FROM Child c LEFT JOIN FETCH c.allergies WHERE c.campusId = :campusId")
    List<Child> findAllWithAllergiesByCampusId(@Param("campusId") String campusId);

    @Query("SELECT c FROM Child c LEFT JOIN FETCH c.allergies WHERE c.id = :id")
    Optional<Child> findByIdWithAllergies(@Param("id") String id);

    @Query("SELECT c FROM Child c LEFT JOIN FETCH c.allergies WHERE c.id = :id AND c.campusId = :campusId")
    Optional<Child> findByIdWithAllergiesAndCampusId(@Param("id") String id, @Param("campusId") String campusId);

    List<Child> findByFamilyId(String familyId);
    List<Child> findByCampusId(String campusId);
}