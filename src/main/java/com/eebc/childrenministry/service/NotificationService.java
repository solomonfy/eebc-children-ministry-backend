package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.NotificationLog;
import com.eebc.childrenministry.entity.ServiceSchedule;
import org.springframework.data.domain.Page;

public interface NotificationService {

    /** Fires immediately when a teacher is assigned to a service. */
    void sendAssignmentNotification(ServiceSchedule assignment);

    /** Sends reminder to all teachers assigned to a given service. */
    void sendManualReminder(String serviceId);

    /** Paginated log — pass null serviceId to get all logs. */
    Page<NotificationLog> getNotifications(String serviceId, int page, int size);
}
