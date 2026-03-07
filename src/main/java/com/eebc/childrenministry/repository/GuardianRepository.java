package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, String> {
    List<Guardian> findByFamilyId(String familyId);
    List<Guardian> findByFamilyIdAndAllowedPickupTrue(String familyId);
    Optional<Guardian> findByUserId(String userId);

    // Find all guardians that have a PIN set
    // Used during kiosk PIN lookup
    @Query("SELECT g FROM Guardian g WHERE g.checkinPinHash IS NOT NULL AND g.active = true")
    List<Guardian> findAllWithPin();

    // Check if phone matches — used for phone-based lookup
    Optional<Guardian> findByPhoneAndActiveTrue(String phone);
}