package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ServiceTypeRequest;
import com.eebc.childrenministry.entity.ServiceTypeConfig;
import com.eebc.childrenministry.repository.ServiceTypeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/service-types")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeConfigRepository repo;

    /**
     * GET /service-types?ministryId=xxx
     * Returns all types for the ministry, sorted by sortOrder.
     * Auto-seeds default types the first time a ministry requests them.
     */
    @GetMapping
    public List<ServiceTypeConfig> list(@RequestParam String ministryId) {
        List<ServiceTypeConfig> types = repo.findByMinistryIdOrderBySortOrderAsc(ministryId);
        if (types.isEmpty()) {
            types = seedDefaults(ministryId);
        }
        return types;
    }

    /**
     * POST /service-types
     * Creates a new service type for a ministry.
     * Code is auto-uppercased and spaces replaced with underscores.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceTypeConfig create(@RequestBody ServiceTypeRequest req) {
        String code = req.code().trim().toUpperCase().replace(" ", "_");
        if (repo.existsByCodeAndMinistryId(code, req.ministryId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A service type with code '" + code + "' already exists.");
        }
        int nextOrder = repo.findByMinistryIdOrderBySortOrderAsc(req.ministryId()).size();
        ServiceTypeConfig c = new ServiceTypeConfig();
        c.setMinistryId(req.ministryId());
        c.setCode(code);
        c.setLabel(req.label().trim());
        c.setColor(req.color() != null && !req.color().isBlank() ? req.color() : "blue");
        c.setSortOrder(nextOrder);
        return repo.save(c);
    }

    /**
     * PATCH /service-types/{id}/toggle
     * Toggles active/inactive on a service type.
     */
    @PatchMapping("/{id}/toggle")
    public ServiceTypeConfig toggle(@PathVariable String id) {
        ServiceTypeConfig c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service type not found"));
        c.setActive(!c.isActive());
        return repo.save(c);
    }

    /**
     * DELETE /service-types/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        repo.deleteById(id);
    }

    // ── Private ───────────────────────────────────────────────────────────

    private List<ServiceTypeConfig> seedDefaults(String ministryId) {
        record Seed(String code, String label, String color, int order) {}
        List<Seed> defaults = List.of(
                new Seed("FRIDAY_EVENING", "Friday Evening",  "violet", 0),
                new Seed("SUNDAY_MORNING", "Sunday Morning",  "amber",  1),
                new Seed("HOLIDAY",        "Holiday Service", "green",  2),
                new Seed("FAMILY_PROGRAM", "Family Program",  "rose",   3),
                new Seed("SEASONAL",       "Seasonal Event",  "orange", 4),
                new Seed("SPECIAL_EVENT",  "Special Event",   "teal",   5)
        );
        for (Seed d : defaults) {
            ServiceTypeConfig c = new ServiceTypeConfig();
            c.setMinistryId(ministryId);
            c.setCode(d.code());
            c.setLabel(d.label());
            c.setColor(d.color());
            c.setSortOrder(d.order());
            repo.save(c);
        }
        return repo.findByMinistryIdOrderBySortOrderAsc(ministryId);
    }
}
