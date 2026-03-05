package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Church;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface ChurchService {
    List<Church> getAllChurches();
    Optional<Church> getChurchById(String id);
}
