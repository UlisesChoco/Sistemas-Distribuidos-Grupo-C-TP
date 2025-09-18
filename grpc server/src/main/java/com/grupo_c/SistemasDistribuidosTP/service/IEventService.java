package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import org.springframework.data.repository.query.Param;

public interface IEventService {
    void removeUserFromUpcomingEvents(User user);
    void markPastEventsAsCompleted();
    Event findById(Long id);
    Event findByIdJoinEventInventory(Long eventId);

}
