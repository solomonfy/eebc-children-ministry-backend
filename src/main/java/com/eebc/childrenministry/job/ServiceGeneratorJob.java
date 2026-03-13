package com.eebc.childrenministry.job;

import com.eebc.childrenministry.repository.ServiceRepository;
import com.eebc.childrenministry.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Runs on startup (seed) and every Monday at 6 AM CST (weekly generation).
 *
 * Requires in application.yml:
 *   app:
 *     default-campus-id: <your-campus-uuid>
 *     default-ministry-id: <your-ministry-uuid>
 *
 * Enable scheduling in your main class:
 *   @EnableScheduling
 */
@Component
@RequiredArgsConstructor
public class ServiceGeneratorJob {

    private static final Logger logger = LoggerFactory.getLogger(ServiceGeneratorJob.class);

    private final ServiceService     serviceService;
    private final ServiceRepository  serviceRepo;

    @Value("${app.default-campus-id}")
    private String defaultCampusId;

    @Value("${app.default-ministry-id}")
    private String defaultMinistryId;

    /**
     * Seeds the next 4 weeks on startup.
     * Uses @EventListener(ApplicationReadyEvent) so all beans are fully wired.
     * Idempotent — existsByServiceDateAndTypeAndCampusId skips duplicates.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedOnStartup() {
        logger.info("=== SERVICE SEED: seeding next 4 weeks of services ===");
        try {
            serviceService.seedNextWeeks(4, defaultCampusId, defaultMinistryId);
            long total = serviceRepo.count();
            logger.info("=== SERVICE SEED COMPLETE: {} total services in DB ===", total);
        } catch (Exception e) {
            logger.error("SERVICE SEED FAILED (non-fatal): {}", e.getMessage(), e);
        }

        // Mark any past services as COMPLETED on startup
        try {
            int updated = serviceService.markPastServicesCompleted();
            if (updated > 0)
                logger.info("=== STARTUP: marked {} past service(s) as COMPLETED ===", updated);
        } catch (Exception e) {
            logger.error("markPastServicesCompleted on startup failed (non-fatal): {}", e.getMessage(), e);
        }

        // Mark today's services as ACTIVE on startup
        try {
            int updated = serviceService.markTodayServicesActive();
            if (updated > 0)
                logger.info("=== STARTUP: marked {} today's service(s) as ACTIVE ===", updated);
        } catch (Exception e) {
            logger.error("markTodayServicesActive on startup failed (non-fatal): {}", e.getMessage(), e);
        }
    }

    /**
     * Every Monday at 6:00 AM CST — generates the next Friday + Sunday
     * that don't already exist.
     *
     * cron format: second minute hour day month weekday
     * "0 0 6 * * MON" = 6:00:00 AM every Monday
     * zone = America/Chicago (CST/CDT)
     */
    @Scheduled(cron = "0 0 6 * * MON", zone = "America/Chicago")
    public void weeklyGeneration() {
        logger.info("=== WEEKLY CRON: generating next Friday + Sunday services ===");
        try {
            serviceService.generateWeek(1, defaultCampusId, defaultMinistryId);
            logger.info("=== WEEKLY CRON COMPLETE ===");
        } catch (Exception e) {
            logger.error("WEEKLY CRON FAILED: {}", e.getMessage(), e);
        }
    }

    /**
     * Daily at midnight CST — marks any services whose date has passed as COMPLETED.
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Chicago")
    public void dailyStatusUpdate() {
        logger.info("=== DAILY STATUS UPDATE: marking past services as COMPLETED, today's as ACTIVE ===");
        try {
            int completed = serviceService.markPastServicesCompleted();
            logger.info("=== DAILY: marked {} past service(s) as COMPLETED ===", completed);
        } catch (Exception e) {
            logger.error("DAILY markPastServicesCompleted FAILED: {}", e.getMessage(), e);
        }
        try {
            int active = serviceService.markTodayServicesActive();
            logger.info("=== DAILY: marked {} today's service(s) as ACTIVE ===", active);
        } catch (Exception e) {
            logger.error("DAILY markTodayServicesActive FAILED: {}", e.getMessage(), e);
        }
    }
}
