package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl extends EventServiceGrpc.EventServiceImplBase implements IEventService {

    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;

    @Autowired
    public EventServiceImpl(
            IEventRepository eventRepository,
            IUserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    EventMapper em = new EventMapper();

    //recibe el evento y la lista de ids de los participantes
    @Override
    public void createEvent (EventServiceClass.EventDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = em.toEntity(event);
        e.setIsCompleted(false);

        Set<User> users = new HashSet<>(userRepository.findAllById(event.getParticipantsIdsList()));
        e.setParticipants(users);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Event created.")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void modifyEvent (EventServiceClass.EventWithParticipantsDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

    }

    @Override
    public void deleteEvent (EventServiceClass.EventRequest request,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event event = eventRepository.findById(request.getId()).orElse(null);

        if (event == null){
            UtilsServiceClass.Response response = UtilsServiceClass.Response
                    .newBuilder()
                    .setMessage("El evento con id: " + request.getId() + " no existe")
                    .setSucceeded(false)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
            return;
        }

        eventRepository.delete(event);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Event deleted.")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

    @Override
    public void assignUserToEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                   StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

    }

    @Override
    public void deleteUserFromEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                     StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

    }

    @Override
    public void getEvent (EventServiceClass.EventRequest request,
                          StreamObserver<EventServiceClass.EventWithParticipantsDto> responseStreamObserver){

    }

    @Override
    public void getEventsWithParticipantsList (UtilsServiceClass.Empty request,
                                               StreamObserver<EventServiceClass.EventsWithParticipantsList> responseStreamObserver){

    }

    @Override
    public void getEventsWithoutParticipantsList (UtilsServiceClass.Empty request,
                                               StreamObserver<EventServiceClass.EventsWithoutParticipantsList> responseStreamObserver){

        List<Event> events = eventRepository.findAll();
        List<EventServiceClass.EventWithoutParticipantsDto> eventsWithoutParticipantsDto = new ArrayList<>();

        for(Event event : events){
            eventsWithoutParticipantsDto.add(em.toDto(event));
        }

        EventServiceClass.EventsWithoutParticipantsList response = EventServiceClass.EventsWithoutParticipantsList.
                newBuilder()
                .addAllEvents(eventsWithoutParticipantsDto)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }



}
