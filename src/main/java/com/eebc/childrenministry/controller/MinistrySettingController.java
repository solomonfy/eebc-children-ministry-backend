package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.MinistrySettingRequest;
import com.eebc.childrenministry.entity.MinistrySetting;
import com.eebc.childrenministry.service.MinistrySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ministry-settings")
public class MinistrySettingController {

    @Autowired
    private MinistrySettingService service;

    @GetMapping
    public ResponseEntity<MinistrySetting> listAll() {
        return ResponseEntity.ok(service.getAllSettings());
    }


    // GET  /ministry-settings/{ministryId}
    // Returns settings for a ministry, auto-creating defaults if none exist
    @GetMapping("/{ministryId}")
    public ResponseEntity<MinistrySetting> get(@PathVariable String ministryId) {
        return ResponseEntity.ok(service.getByMinistryId(ministryId));
    }

    // PUT  /ministry-settings/{ministryId}
    // Full or partial update — only non-null fields are applied
    @PutMapping("/{ministryId}")
    public ResponseEntity<MinistrySetting> update(
            @PathVariable String ministryId,
            @RequestBody MinistrySettingRequest req) {
        return ResponseEntity.ok(service.update(ministryId, req));
    }

    // PATCH /ministry-settings/{ministryId}/toggle/{fieldName}
    // Flips a single boolean field — used by the toggle buttons on the frontend
    // fieldName: enableLastNameCheckin | enablePhoneNumberCheckin |
    //            enableQrCodeCheckin | enablePinCodeCheckin |
    //            autoCheckoutEnabled | requirePickupCode | allowGuestCheckin |
    //            lessonsEnabled | parentRecapsEnabled | incidentsEnabled
    @PatchMapping("/{ministryId}/toggle/{fieldName}")
    public ResponseEntity<MinistrySetting> toggle(
            @PathVariable String ministryId,
            @PathVariable String fieldName) {
        return ResponseEntity.ok(service.toggle(ministryId, fieldName));
    }

    // POST /ministry-settings/{ministryId}/reset
    // Resets all settings back to system defaults
    @PostMapping("/{ministryId}/reset")
    public ResponseEntity<Map<String, Object>> reset(@PathVariable String ministryId) {
        MinistrySetting s = service.reset(ministryId);
        return ResponseEntity.ok(Map.of("message", "Settings reset to defaults", "settings", s));
    }
}
