package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.*;
import com.eebc.childrenministry.repository.*;
import com.eebc.childrenministry.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final ServiceRepository         serviceRepo;
    private final ServiceScheduleRepository scheduleRepo;
    private final UserRepository            userRepo;
    private final ClassroomRepository       classroomRepo;
    private final NotificationLogRepository notificationLogRepo;

    // Optional — null if spring.mail.username is not configured
//    @Autowired(required = false)
//    private MailSender mailSender;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${notification.from-name:EEBC Children's Ministry}")
    private String fromName;

    // ── Assignment notification ─────────────────────────────────────────────

    @Override
    public void sendAssignmentNotification(ServiceSchedule assignment) {
        try {
            com.eebc.childrenministry.entity.Service service =
                    serviceRepo.findById(assignment.getServiceId()).orElse(null);
            User teacher = userRepo.findById(assignment.getTeacherId()).orElse(null);
            Classroom classroom = classroomRepo.findById(assignment.getClassroomId()).orElse(null);

            if (service == null || teacher == null) {
                logger.warn("Assignment notification skipped — missing service or teacher data");
                return;
            }

            String classroomName = classroom != null ? classroom.getName() : "your classroom";
            String subject = "You've been assigned to serve at " + service.getName();
            String body = buildAssignmentBody(teacher.getFirstName(), service, classroomName);

            sendEmail(teacher, service, classroomName, subject, body, "ASSIGNMENT");
            sendSms(teacher, service, classroomName, "ASSIGNMENT");

        } catch (Exception e) {
            logger.error("Unexpected error in sendAssignmentNotification: {}", e.getMessage());
        }
    }

    // ── Manual / cron reminder ──────────────────────────────────────────────

    @Override
    public void sendManualReminder(String serviceId) {
        try {
            com.eebc.childrenministry.entity.Service service =
                    serviceRepo.findById(serviceId).orElse(null);
            if (service == null) {
                logger.warn("Reminder skipped — service {} not found", serviceId);
                return;
            }

            List<ServiceSchedule> assignments = scheduleRepo.findByServiceId(serviceId);
            for (ServiceSchedule a : assignments) {
                User teacher = userRepo.findById(a.getTeacherId()).orElse(null);
                Classroom classroom = classroomRepo.findById(a.getClassroomId()).orElse(null);
                if (teacher == null) continue;

                String classroomName = classroom != null ? classroom.getName() : "your classroom";
                String subject = "Reminder: You're serving at " + service.getName();
                String body = buildReminderBody(teacher.getFirstName(), service, classroomName);

                sendEmail(teacher, service, classroomName, subject, body, "REMINDER");
                sendSms(teacher, service, classroomName, "REMINDER");
            }
        } catch (Exception e) {
            logger.error("Unexpected error in sendManualReminder for service {}: {}", serviceId, e.getMessage());
        }
    }

    // ── Daily cron reminder: 9 AM CST ───────────────────────────────────────

    @Scheduled(cron = "0 0 9 * * ?", zone = "America/Chicago")
    public void sendScheduledReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<com.eebc.childrenministry.entity.Service> tomorrowServices =
                serviceRepo.findByServiceDate(tomorrow);
        logger.info("Scheduled reminder job: found {} service(s) for {}", tomorrowServices.size(), tomorrow);
        for (com.eebc.childrenministry.entity.Service s : tomorrowServices) {
            sendManualReminder(s.getId());
        }
    }

    // ── Log query ──────────────────────────────────────────────────────────

    @Override
    public Page<NotificationLog> getNotifications(String serviceId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (serviceId != null && !serviceId.isBlank()) {
            return notificationLogRepo.findByServiceIdOrderBySentAtDesc(serviceId, pageable);
        }
        return notificationLogRepo.findAllByOrderBySentAtDesc(pageable);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private void sendEmail(User teacher, com.eebc.childrenministry.entity.Service service,
                           String classroomName, String subject, String body, String type) {
        NotificationLog log = buildLog(teacher, service, classroomName, "EMAIL", type);

        // Skip if teacher opted out or no email
        if (Boolean.FALSE.equals(teacher.getNotifyEmail())) {
            log.setStatus("SKIPPED");
            log.setErrorMessage("Teacher opted out of email notifications");
            notificationLogRepo.save(log);
            return;
        }
        if (teacher.getEmail() == null || teacher.getEmail().isBlank()) {
            log.setStatus("SKIPPED");
            log.setErrorMessage("No email address on file");
            notificationLogRepo.save(log);
            return;
        }
        if (mailSender == null || mailUsername == null || mailUsername.isBlank()) {
            log.setStatus("SKIPPED");
            log.setErrorMessage("Email provider not configured (set MAIL_USERNAME + MAIL_PASSWORD)");
            notificationLogRepo.save(log);
            logger.info("Email SKIPPED for {} — mail not configured", teacher.getEmail());
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromName + " <" + mailUsername + ">");
            msg.setTo(teacher.getEmail());
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.setStatus("SENT");
            logger.info("Email SENT to {} ({})", teacher.getEmail(), type);
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            logger.error("Email FAILED to {}: {}", teacher.getEmail(), e.getMessage());
        }

        notificationLogRepo.save(log);
    }

    private void sendSms(User teacher, com.eebc.childrenministry.entity.Service service,
                         String classroomName, String type) {
        NotificationLog log = buildLog(teacher, service, classroomName, "SMS", type);

        if (Boolean.FALSE.equals(teacher.getNotifySms())) {
            log.setStatus("SKIPPED");
            log.setErrorMessage("Teacher opted out of SMS notifications");
            notificationLogRepo.save(log);
            return;
        }
        if (teacher.getPhone() == null || teacher.getPhone().isBlank()) {
            log.setStatus("SKIPPED");
            log.setErrorMessage("No phone number on file");
            notificationLogRepo.save(log);
            return;
        }

        // SMS provider not yet configured — log as SKIPPED
        log.setStatus("SKIPPED");
        log.setErrorMessage("SMS provider not configured (Twilio credentials required)");
        notificationLogRepo.save(log);
        logger.info("SMS SKIPPED for {} — provider not configured", teacher.getPhone());
    }

    private NotificationLog buildLog(User teacher, com.eebc.childrenministry.entity.Service service,
                                     String classroomName, String channel, String type) {
        NotificationLog log = new NotificationLog();
        log.setTeacherId(teacher.getId());
        log.setTeacherName(teacher.getFirstName() + " " + teacher.getLastName());
        log.setTeacherEmail(teacher.getEmail());
        log.setTeacherPhone(teacher.getPhone());
        log.setServiceId(service.getId());
        log.setServiceName(service.getName());
        log.setServiceDate(service.getServiceDate() != null ? service.getServiceDate().toString() : null);
        log.setClassroomName(classroomName);
        log.setChannel(channel);
        log.setType(type);
        return log;
    }

    private String buildAssignmentBody(String firstName,
                                       com.eebc.childrenministry.entity.Service service,
                                       String classroomName) {
        return "Hi " + firstName + ",\n\n"
                + "You've been assigned to serve at " + service.getName() + ".\n\n"
                + "Date:      " + service.getServiceDate() + "\n"
                + "Time:      " + service.getStartTime() + " – " + service.getEndTime() + "\n"
                + "Classroom: " + classroomName + "\n\n"
                + "Thank you for your service!\n\n"
                + fromName;
    }

    private String buildReminderBody(String firstName,
                                     com.eebc.childrenministry.entity.Service service,
                                     String classroomName) {
        return "Hi " + firstName + ",\n\n"
                + "This is a reminder that you're scheduled to serve tomorrow.\n\n"
                + "Service:   " + service.getName() + "\n"
                + "Date:      " + service.getServiceDate() + "\n"
                + "Time:      " + service.getStartTime() + " – " + service.getEndTime() + "\n"
                + "Classroom: " + classroomName + "\n\n"
                + "Thank you for your service!\n\n"
                + fromName;
    }
}
