package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ServiceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceEventRepository extends JpaRepository<ServiceEvent, String> {
}
