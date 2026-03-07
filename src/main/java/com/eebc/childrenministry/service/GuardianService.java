package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Guardian;

import java.util.List;
import java.util.Optional;

public interface GuardianService {
    void setPin(String guardianId, String rawPin);
    void removePin(String guardianId);
    Optional<Guardian> findByPin(String rawPin);
    boolean verifyPin(String guardianId, String rawPin);
    List<Guardian> getByFamily(String familyId);
    List<Guardian> getAuthorizedPickup(String familyId);
}
