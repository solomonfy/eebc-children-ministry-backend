package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Guardian;
import com.eebc.childrenministry.repository.GuardianRepository;
import com.eebc.childrenministry.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    // ── Set or update a guardian's kiosk PIN ──
    public void setPin(String guardianId, String rawPin) {
        // Validate PIN — 4 to 6 digits only
        if (rawPin == null || !rawPin.matches("\\d{4,6}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "PIN must be 4–6 digits"
            );
        }

        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Guardian not found"
                ));

        guardian.setCheckinPinHash(encoder.encode(rawPin));
        guardian.setPinSetAt(LocalDateTime.now());
        guardian.setUpdatedAt(LocalDateTime.now());
        guardianRepository.save(guardian);
    }

    @Override
    // ── Remove a guardian's PIN ───────────────
    public void removePin(String guardianId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Guardian not found"
                ));
        guardian.setCheckinPinHash(null);
        guardian.setPinSetAt(null);
        guardian.setUpdatedAt(LocalDateTime.now());
        guardianRepository.save(guardian);
    }

    // ── Kiosk: look up guardian by raw PIN ────
    // Iterates guardians with a PIN set and
    // BCrypt-matches — returns first match
    @Override
    public Optional<Guardian> findByPin(String rawPin) {
        if (rawPin == null || rawPin.isBlank()) return Optional.empty();

        return guardianRepository.findAllWithPin()
                .stream()
                .filter(g -> encoder.matches(rawPin, g.getCheckinPinHash()))
                .findFirst();
    }

    // ── Verify PIN without exposing guardian ──
    @Override
    public boolean verifyPin(String guardianId, String rawPin) {
        return guardianRepository.findById(guardianId)
                .map(g -> g.getCheckinPinHash() != null &&
                        encoder.matches(rawPin, g.getCheckinPinHash()))
                .orElse(false);
    }

    @Override
    public List<Guardian> getByFamily(String familyId) {
        return guardianRepository.findByFamilyId(familyId);
    }

    @Override
    public List<Guardian> getAuthorizedPickup(String familyId) {
        return guardianRepository.findByFamilyIdAndAllowedPickupTrue(familyId);
    }

}
