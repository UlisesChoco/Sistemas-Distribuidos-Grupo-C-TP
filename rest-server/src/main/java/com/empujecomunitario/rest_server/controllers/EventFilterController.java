package com.empujecomunitario.rest_server.controllers;

import org.springframework.web.bind.annotation.*;
import com.empujecomunitario.rest_server.entity.EventFilter;
import com.empujecomunitario.rest_server.service.IEventFilterService;

import java.util.List;

@RestController
@RequestMapping("/api/filters")
public class EventFilterController {
    private final IEventFilterService service;

    public EventFilterController(IEventFilterService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public List<EventFilter> getFilters(@PathVariable Long userId) {
        return service.getFiltersByUser(userId);
    }

    @PostMapping
    public EventFilter createFilter(@RequestBody EventFilter filter) {
        return service.createFilter(filter);
    }

    @DeleteMapping("/{id}")
    public void deleteFilter(@PathVariable Long id) {
        service.deleteFilter(id);
    }
}
