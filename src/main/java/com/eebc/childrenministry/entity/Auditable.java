package com.eebc.childrenministry.entity;

import com.eebc.childrenministry.audit.AuditableEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class for all audited entities.
 *
 * To enable audit history on any entity, simply extend this class:
 *
 *   @Entity
 *   @Table(name = "rooms")
 *   public class Room extends Auditable { ... }
 *
 * That's it — INSERT, UPDATE, DELETE are all captured automatically.
 * No changes needed in any service or controller.
 */
@MappedSuperclass
@EntityListeners(AuditableEntityListener.class)
public abstract class Auditable {
    // Intentionally empty — the listener does all the work
}
