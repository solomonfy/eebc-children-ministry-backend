package com.eebc.childrenministry.audit;

import com.eebc.childrenministry.config.RequestContext;
import com.eebc.childrenministry.entity.Auditable;
import com.eebc.childrenministry.entity.AuditHistory;
import com.eebc.childrenministry.repository.AuditHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * JPA Entity Listener — automatically audits every INSERT, UPDATE, DELETE
 * across all entities that extend Auditable (or use @EntityListeners directly).
 *
 * Wire up by adding to your base class:
 *   @MappedSuperclass
 *   @EntityListeners(AuditableEntityListener.class)
 *   public abstract class Auditable { ... }
 *
 * Then have all your entities extend Auditable.
 */
@Component
public class AuditableEntityListener {

    private static final Logger logger = LoggerFactory.getLogger(AuditableEntityListener.class);

    // Static references — JPA listeners are not Spring beans by default,
    // so we use static injection via SpringBeanProvider
    private static AuditHistoryRepository auditRepo;
    private static ApplicationContext applicationContext;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Called by SpringBeanProvider after context is ready
    public static void setAuditRepo(AuditHistoryRepository repo) {
        auditRepo = repo;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    /** Called by Auditable#captureAuditSnapshot() — must be static and public. */
    public static String serializeToJson(Object entity) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.convertValue(entity, Map.class);
            map.remove("passwordHash");
            map.remove("password_hash");
            map.remove("password");
            map.remove("pin");
            map.remove("pinHash");
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{\"error\": \"serialization failed\"}";
        }
    }

    // ── JPA Hooks ─────────────────────────────

    @PrePersist
    public void onInsert(Object entity) {
        save(entity, "INSERT", null, toJson(entity),
                describeAction(entity, "INSERT"));
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        String oldValue = null;
        if (entity instanceof Auditable auditable) {
            oldValue = auditable.getAuditSnapshot();
        }
        save(entity, "UPDATE", oldValue, toJson(entity),
                describeAction(entity, "UPDATE"));
    }

    @PreRemove
    public void onDelete(Object entity) {
        save(entity, "DELETE", toJson(entity), null,
                describeAction(entity, "DELETE"));
    }

    // ── Helpers ───────────────────────────────

    private void save(Object entity, String action,
                      String oldValue, String newValue, String description) {
        if (auditRepo == null) return; // not yet wired (e.g. during startup)

        try {
            String entityName = entity.getClass().getSimpleName();
            String entityId   = getEntityId(entity);

            String changedBy     = null;
            String changedByName = "System";
            String ipAddress     = null;
            String userAgent     = null;

            try {
                if (applicationContext != null) {
                    RequestContext rc = applicationContext.getBean(RequestContext.class);
                    changedBy     = rc.getUserId();
                    changedByName = rc.getUserName() != null ? rc.getUserName() : "System";
                    ipAddress     = rc.getIpAddress();
                    userAgent     = rc.getUserAgent();
                }
            } catch (Exception ignored) {
                // RequestContext not available outside a request (e.g. cron jobs)
                changedByName = "System / Scheduled";
            }

            AuditHistory audit = new AuditHistory();
            audit.setEntityName(entityName);
            audit.setEntityId(entityId != null ? entityId : "unknown");
            audit.setAction(action);
            audit.setDescription(description);
            audit.setChangedBy(changedBy);
            audit.setChangedByName(changedByName);
            audit.setOldValue(oldValue);
            audit.setNewValue(newValue);
            audit.setIpAddress(ipAddress);
            audit.setUserAgent(userAgent);

            auditRepo.save(audit);

        } catch (Exception e) {
            // Audit failure must NEVER break the main transaction
            logger.error("Audit write failed for {} {}: {}", action,
                    entity.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Extracts the entity ID for audit grouping.
     * ClassroomTeacher uses userId so audit rows can be queried by teacher.
     */
    private String getEntityId(Object entity) {
        // For ClassroomTeacher, group audit rows by the teacher's userId
        if ("ClassroomTeacher".equals(entity.getClass().getSimpleName())) {
            try {
                Field f = entity.getClass().getDeclaredField("userId");
                f.setAccessible(true);
                Object val = f.get(entity);
                return val != null ? val.toString() : null;
            } catch (Exception e) {
                logger.warn("Could not extract ClassroomTeacher.userId: {}", e.getMessage());
            }
        }

        try {
            Class<?> clazz = entity.getClass();
            while (clazz != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Id.class)) {
                        f.setAccessible(true);
                        Object val = f.get(entity);
                        return val != null ? val.toString() : null;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            logger.warn("Could not extract entity id: {}", e.getMessage());
        }
        return null;
    }

    private String toJson(Object entity) {
        return serializeToJson(entity);
    }

    /**
     * Generates a human-readable description matching the style in the screenshot:
     * "Added as new", "Status updated", "Teacher assigned", etc.
     */
    private String describeAction(Object entity, String action) {
        String name = entity.getClass().getSimpleName();
        return switch (action) {
            case "INSERT" -> switch (name) {
                case "Service"         -> "Service created";
                case "ServiceSchedule" -> "Teacher assigned to service";
                default                -> "Added as new";
            };
            case "DELETE" -> switch (name) {
                case "Service"         -> "Service deleted";
                case "ServiceSchedule" -> "Teacher removed from service";
                default                -> name + " removed";
            };
            case "UPDATE" -> switch (name) {
                case "ClassroomTeacher"  -> "Teacher assigned";
                case "RoomAssignment"    -> "Room assigned";
                case "User"              -> "User updated";
                case "Room"              -> "Room updated";
                case "Classroom"         -> "Classroom updated";
                case "Guardian"          -> "Guardian updated";
                case "Child"             -> "Child updated";
                case "Service"           -> "Service updated";
                case "ServiceSchedule"   -> "Assignment updated";
                default                  -> name + " updated";
            };
            default -> action;
        };
    }
}
