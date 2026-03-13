package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    List<Service> findAllByOrderByServiceDateAsc();

    List<Service> findByStatus(String status);

    List<Service> findByCampusId(String campusId);

    List<Service> findByMinistryId(String ministryId);

    List<Service> findByServiceDateBetweenOrderByServiceDateAsc(
            LocalDate from, LocalDate to);

    List<Service> findByServiceDateGreaterThanEqualOrderByServiceDateAsc(
            LocalDate from);

    // Used by cron + seed to avoid duplicates
    boolean existsByServiceDateAndTypeAndCampusId(
            LocalDate serviceDate, String type, String campusId);

    Optional<Service> findByServiceDateAndTypeAndCampusId(
            LocalDate serviceDate, String type, String campusId);

    List<Service> findByType(String type);

    List<Service> findByServiceDate(LocalDate serviceDate);

    List<Service> findByServiceDateBeforeAndStatusIn(LocalDate date, List<String> statuses);

    List<Service> findByServiceDateAndStatusIn(LocalDate date, List<String> statuses);
}
