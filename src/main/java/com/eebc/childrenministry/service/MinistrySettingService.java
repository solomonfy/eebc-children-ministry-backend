package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.MinistrySettingRequest;
import com.eebc.childrenministry.entity.MinistrySetting;
import com.eebc.childrenministry.repository.MinistrySettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MinistrySettingService {

    @Autowired
    private MinistrySettingRepository repo;

    // ── Get or auto-create defaults ────────────
    public MinistrySetting getByMinistryId(String ministryId) {
        final MinistrySetting ministrySetting = repo.findByMinistryId(ministryId)
                .orElseGet(() -> createDefaults(ministryId));
        return ministrySetting;
    }

    // ── Full update (PUT) ──────────────────────
    public MinistrySetting update(String ministryId, MinistrySettingRequest req) {
        MinistrySetting settings = repo.findByMinistryId(ministryId)
                .orElseGet(() -> createDefaults(ministryId));

        if (req.checkinMode()               != null) settings.setCheckinMode(req.checkinMode());
        if (req.enableLastNameCheckin()     != null) settings.setEnableLastNameCheckin(req.enableLastNameCheckin());
        if (req.enablePhoneNumberCheckin()  != null) settings.setEnablePhoneNumberCheckin(req.enablePhoneNumberCheckin());
        if (req.enableQrCodeCheckin()       != null) settings.setEnableQrCodeCheckin(req.enableQrCodeCheckin());
        if (req.enablePinCodeCheckin()      != null) settings.setEnablePinCodeCheckin(req.enablePinCodeCheckin());
        if (req.checkinEarlyMinutes()       != null) settings.setCheckinEarlyMinutes(req.checkinEarlyMinutes());
        if (req.checkinLateMinutes()        != null) settings.setCheckinLateMinutes(req.checkinLateMinutes());
        if (req.autoCheckoutEnabled()       != null) settings.setAutoCheckoutEnabled(req.autoCheckoutEnabled());
        if (req.autoCheckoutMinutes()       != null) settings.setAutoCheckoutMinutes(req.autoCheckoutMinutes());
        if (req.pickupCodeLength()          != null) settings.setPickupCodeLength(req.pickupCodeLength());
        if (req.requirePickupCode()         != null) settings.setRequirePickupCode(req.requirePickupCode());
        if (req.allowGuestCheckin()         != null) settings.setAllowGuestCheckin(req.allowGuestCheckin());
        if (req.lessonsEnabled()            != null) settings.setLessonsEnabled(req.lessonsEnabled());
        if (req.parentRecapsEnabled()       != null) settings.setParentRecapsEnabled(req.parentRecapsEnabled());
        if (req.incidentsEnabled()          != null) settings.setIncidentsEnabled(req.incidentsEnabled());

        return repo.save(settings);
    }

    // ── Toggle a single boolean field ─────────
    // fieldName must match the exact Java field name on MinistrySettings
    public MinistrySetting toggle(String ministryId, String fieldName) {
        MinistrySetting s = getByMinistryId(ministryId);
        switch (fieldName) {
            case "enableLastNameCheckin"    -> s.setEnableLastNameCheckin(!s.getEnableLastNameCheckin());
            case "enablePhoneNumberCheckin" -> s.setEnablePhoneNumberCheckin(!s.getEnablePhoneNumberCheckin());
            case "enableQrCodeCheckin"      -> s.setEnableQrCodeCheckin(!s.getEnableQrCodeCheckin());
            case "enablePinCodeCheckin"     -> s.setEnablePinCodeCheckin(!s.getEnablePinCodeCheckin());
            case "autoCheckoutEnabled"      -> s.setAutoCheckoutEnabled(!s.getAutoCheckoutEnabled());
            case "requirePickupCode"        -> s.setRequirePickupCode(!s.getRequirePickupCode());
            case "allowGuestCheckin"        -> s.setAllowGuestCheckin(!s.getAllowGuestCheckin());
            case "lessonsEnabled"           -> s.setLessonsEnabled(!s.getLessonsEnabled());
            case "parentRecapsEnabled"      -> s.setParentRecapsEnabled(!s.getParentRecapsEnabled());
            case "incidentsEnabled"         -> s.setIncidentsEnabled(!s.getIncidentsEnabled());
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unknown toggle field: " + fieldName);
        }
        return repo.save(s);
    }

    // ── Reset to defaults ──────────────────────
    public MinistrySetting reset(String ministryId) {
        MinistrySetting s = getByMinistryId(ministryId);
        applyDefaults(s);
        return repo.save(s);
    }

    // ── Private helpers ────────────────────────
    private MinistrySetting createDefaults(String ministryId) {
        MinistrySetting s = new MinistrySetting();
        s.setMinistryId(ministryId);
        applyDefaults(s);
        return repo.save(s);
    }

    private void applyDefaults(MinistrySetting s) {
        s.setCheckinMode("PRINT_ONLY");
        s.setEnableLastNameCheckin(true);
        s.setEnablePhoneNumberCheckin(true);
        s.setEnableQrCodeCheckin(true);
        s.setEnablePinCodeCheckin(true);
        s.setCheckinEarlyMinutes(30);
        s.setCheckinLateMinutes(15);
        s.setAutoCheckoutEnabled(false);
        s.setAutoCheckoutMinutes(30);
        s.setPickupCodeLength(4);
        s.setRequirePickupCode(true);
        s.setAllowGuestCheckin(true);
        s.setLessonsEnabled(true);
        s.setParentRecapsEnabled(true);
        s.setIncidentsEnabled(true);
    }
}
