package com.empujecomunitario.rest_server.service;

import org.springframework.stereotype.Service;

import com.empujecomunitario.rest_server.entity.EventFilter;
import com.empujecomunitario.rest_server.repository.IEventFilterRepository;

import java.util.List;

@Service
public class IEventFilterService {
    private final IEventFilterRepository repository;

    public IEventFilterService(IEventFilterRepository repository) {
        this.repository = repository;
    }

    public List<EventFilter> getFiltersByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public EventFilter createFilter(EventFilter filter) {
        return repository.save(filter);
    }

    public void deleteFilter(Long id) {
        repository.deleteById(id);
    }
}
