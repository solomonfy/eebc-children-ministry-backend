package com.eebc.childrenministry.audit;

import com.eebc.childrenministry.config.RequestContext;
import com.eebc.childrenministry.entity.AuditHistory;
import com.eebc.childrenministry.repository.AuditHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
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
    private static RequestContext requestContext;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Called by SpringBeanProvider after context is ready
    public static void setAuditRepo(AuditHistoryRepository repo) {
        auditRepo = repo;
    }

    public static void setRequestContext(RequestContext ctx) {
        requestContext = ctx;
    }

    // ── JPA Hooks ─────────────────────────────

    @PrePersist
    public void onInsert(Object entity) {
        save(entity, "INSERT", null, toJson(entity),
                describeAction(entity, "INSERT"));
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        // old_value not available in @PreUpdate without a second query —
        // we store the current (post-change) snapshot as new_value
        // For true old/new diffs, entities can implement Auditable#getAuditSnapshot()
        save(entity, "UPDATE", null, toJson(entity),
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
                if (requestContext != null) {
                    changedBy     = requestContext.getUserId();
                    changedByName = requestContext.getUserName() != null
                            ? requestContext.getUserName() : "System";
                    ipAddress     = requestContext.getIpAddress();
                    userAgent     = requestContext.getUserAgent();
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
     * Extracts the @Id field value via reflection.
     */
    private String getEntityId(Object entity) {
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

    /**
     * Serializes entity to JSON, masking sensitive fields.
     */
    private String toJson(Object entity) {
        try {
            // Convert to map so we can mask sensitive fields
            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.convertValue(entity, Map.class);

            // Always mask password — safety net even if @JsonIgnore is present
            map.remove("passwordHash");
            map.remove("password_hash");
            map.remove("password");
            map.remove("pin");
            map.remove("pinHash");

            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            logger.warn("Could not serialize entity to JSON: {}", e.getMessage());
            return "{\"error\": \"serialization failed\"}";
        }
    }

    /**
     * Generates a human-readable description matching the style in the screenshot:
     * "Added as new", "Status updated", "Teacher assigned", etc.
     */
    private String describeAction(Object entity, String action) {
        String name = entity.getClass().getSimpleName();
        return switch (action) {
            case "INSERT" -> "Added as new";
            case "DELETE" -> name + " removed";
            case "UPDATE" -> switch (name) {
                case "ClassroomTeacher" -> "Teacher assigned";
                case "RoomAssignment"   -> "Room assigned";
                case "User"             -> "User updated";
                case "Room"             -> "Room updated";
                case "Classroom"        -> "Classroom updated";
                case "Guardian"         -> "Guardian updated";
                case "Child"            -> "Child updated";
                default                 -> name + " updated";
            };
            default -> action;
        };
    }
}
