package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@NoArgsConstructor
@Getter
@Setter
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "teacher_email")
    private String teacherEmail;

    @Column(name = "teacher_phone")
    private String teacherPhone;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "service_date")
    private String serviceDate;

    @Column(name = "classroom_name")
    private String classroomName;

    /** EMAIL | SMS */
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String channel;

    /** ASSIGNMENT | REMINDER */
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String type;

    /** SENT | FAILED | SKIPPED */
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private String status;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
}
