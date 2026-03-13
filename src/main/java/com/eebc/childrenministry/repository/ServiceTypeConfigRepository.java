package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ServiceTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceTypeConfigRepository extends JpaRepository<ServiceTypeConfig, String> {

    List<ServiceTypeConfig> findByMinistryIdOrderBySortOrderAsc(String ministryId);

    boolean existsByCodeAndMinistryId(String code, String ministryId);
}
