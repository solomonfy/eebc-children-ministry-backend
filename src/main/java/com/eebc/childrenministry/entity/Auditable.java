package com.eebc.childrenministry.entity;

import com.eebc.childrenministry.audit.AuditableEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;

/**
 * Base class for all audited entities.
 * Captures a JSON snapshot on load so @PreUpdate can diff old vs new values.
 * To enable audit history on any entity, simply extend this class:
 *
 *   @Entity
 *   @Table(name = "rooms")
 *   public class Room extends Auditable { ... }
 *
 * That's it — INSERT, UPDATE, DELETE are all captured automatically.
 * No changes needed in any service or controller. */
@MappedSuperclass
@EntityListeners(AuditableEntityListener.class)
public abstract class Auditable {

    /** Populated by @PostLoad — holds the state as it was when loaded from DB. */
    @Transient
    private String _auditSnapshot;

    @PostLoad
    public void captureAuditSnapshot() {
        try {
            this._auditSnapshot = AuditableEntityListener.serializeToJson(this);
        } catch (Exception ignored) {}
    }

    @JsonIgnore
    public String getAuditSnapshot() { return _auditSnapshot; }
}
