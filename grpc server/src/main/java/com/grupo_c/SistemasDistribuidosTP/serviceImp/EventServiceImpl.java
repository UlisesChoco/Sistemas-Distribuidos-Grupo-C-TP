package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements IEventService {

    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(
            IEventRepository eventRepository,
            IUserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = new EventMapper();
    }

    @Override
    public void removeUserFromUpcomingEvents(User user) {
        List<Event> events = eventRepository.findUpcomingEventsByParticipant(user.getId());

        for(Event e: events){
            e.getParticipants().removeIf(p->p.getId().equals(user.getId()));
            eventRepository.save(e);
        }
    }
}
