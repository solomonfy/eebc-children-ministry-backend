package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.MinistrySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MinistrySettingRepository extends JpaRepository<MinistrySetting, String> {

    Optional<MinistrySetting> findByMinistryId(String ministryId);

    boolean existsByMinistryId(String ministryId);
}
