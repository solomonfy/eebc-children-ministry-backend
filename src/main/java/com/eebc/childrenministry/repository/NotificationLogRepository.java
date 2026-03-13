package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {

    Page<NotificationLog> findByServiceIdOrderBySentAtDesc(String serviceId, Pageable pageable);

    Page<NotificationLog> findAllByOrderBySentAtDesc(Pageable pageable);
}
