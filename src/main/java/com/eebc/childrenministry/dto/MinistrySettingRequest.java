package com.eebc.childrenministry.dto;

public record MinistrySettingRequest(
        String  checkinMode,

        // Check-in methods
        Boolean enableLastNameCheckin,
        Boolean enablePhoneNumberCheckin,
        Boolean enableQrCodeCheckin,
        Boolean enablePinCodeCheckin,

        // Timing
        Integer checkinEarlyMinutes,
        Integer checkinLateMinutes,

        // Auto checkout
        Boolean autoCheckoutEnabled,
        Integer autoCheckoutMinutes,

        // Pickup
        Integer pickupCodeLength,
        Boolean requirePickupCode,
        Boolean allowGuestCheckin,

        // Features
        Boolean lessonsEnabled,
        Boolean parentRecapsEnabled,
        Boolean incidentsEnabled
) {}
