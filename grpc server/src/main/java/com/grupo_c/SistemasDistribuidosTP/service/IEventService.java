package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;

public interface IEventService {
    void removeUserFromUpcomingEvents(User user);
    void markPastEventsAsCompleted();
}
