package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.MinistrySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MinistrySettingRepository extends JpaRepository<MinistrySetting, String> {
}
