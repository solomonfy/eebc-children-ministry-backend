package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.MinistrySetting;
import com.eebc.childrenministry.repository.MinistrySettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ministry_settings")
@RequiredArgsConstructor
public class MinistrySettingController {

    private final MinistrySettingRepository repository;

    @GetMapping
    public ResponseEntity<List<MinistrySetting>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<MinistrySetting> create(@RequestBody MinistrySetting s) {
        MinistrySetting saved = repository.save(s);
        return ResponseEntity.ok(saved);
    }
}
