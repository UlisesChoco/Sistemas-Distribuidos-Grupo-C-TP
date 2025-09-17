package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventServiceGrpcImpl extends EventServiceGrpc.EventServiceImplBase{

    private final IEventService eventService;
    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceGrpcImpl(
            IEventRepository eventRepository,
            IUserRepository userRepository,
            IEventService eventService
    ) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = new EventMapper();
    }


    //recibe el evento y la lista de ids de los participantes
    @Override
    public void createEvent (EventServiceClass.EventDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventMapper.toEntity(event);
        e.setIsCompleted(false);

        List<Long> ids = new ArrayList<>();
        for (EventServiceClass.ParticipantDto participant : event.getParticipantsList()){
            ids.add(participant.getId());
        }

        Set<User> participants = new HashSet<>(userRepository.findAllById(ids));
        e.setParticipants(participants);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Creado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void modifyEvent (EventServiceClass.EventWithParticipantsDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findById(event.getId()).orElse(null);

        if(e == null){
            UtilsServiceClass.Response response = UtilsServiceClass.Response
                    .newBuilder()
                    .setMessage("El Evento no existe")
                    .setSucceeded(false)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        }

        List<Long> ids = new ArrayList<>();
        for (EventServiceClass.ParticipantDto participant : event.getParticipantsList()){
            ids.add(participant.getId());
        }

        Set<User> participants = new HashSet<>(userRepository.findAllById(ids));

        e.setName(event.getName());
        e.setDescription(event.getDescription());

        //si la fecha es distinta se cambia
        if (!e.getDate().equals(eventMapper.toLocalDateTime(event.getDate()))){

            e.setDate(eventMapper.toLocalDateTime(event.getDate()));
        }

        e.setIsCompleted(event.getIsCompleted());
        e.setParticipants(participants);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Modificado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
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

        if (event.getIsCompleted()){
            UtilsServiceClass.Response response = UtilsServiceClass.Response
                    .newBuilder()
                    .setMessage("El evento ya finalizó, no se puede eliminar")
                    .setSucceeded(false)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
            return;
        }

        eventRepository.delete(event);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Borrado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

    @Override
    public void assignUserToEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                   StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findByIdJoinParticipants(request.getEventId()).orElse(null);
        User u = userRepository.findById(request.getUserId()).orElse(null);

        if(e==null || u==null){
            UtilsServiceClass.Response response = UtilsServiceClass.Response
                    .newBuilder()
                    .setMessage("Id del evento o del participante invalido")
                    .setSucceeded(false)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
            return;
        }

        e.getParticipants().add(u);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("El usuario " + u.getUsername() + " fue añadido al evento " + e.getName())
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteUserFromEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                     StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findByIdJoinParticipants(request.getEventId()).orElse(null);
        User u = userRepository.findByIdJoinEvents(request.getUserId()).orElse(null);

        if(e==null || u==null){
            UtilsServiceClass.Response response = UtilsServiceClass.Response
                    .newBuilder()
                    .setMessage("Id del evento o del participante invalido")
                    .setSucceeded(false)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
            return;
        }

        e.getParticipants().removeIf(p->p.getId().equals(u.getId()));

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("El usuario " + u.getUsername() + " fue eliminado del evento " + e.getName())
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEvent (EventServiceClass.EventRequest request,
                          StreamObserver<EventServiceClass.EventWithParticipantsDto> responseStreamObserver){

        Event e = eventRepository.findByIdJoinParticipants(request.getId()).orElse(null);

        EventServiceClass.EventWithParticipantsDto response = eventMapper.toEventWithParticipantsDto(e);

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEventsWithParticipantsList (UtilsServiceClass.Empty request,
                                               StreamObserver<EventServiceClass.EventsWithParticipantsList> responseStreamObserver){

        eventService.markPastEventsAsCompleted();

        List<Event> events = eventRepository.findAllJoinParticipants();
        List<EventServiceClass.EventWithParticipantsDto> eventsWithParticipantsDto = new ArrayList<>();

        for(Event event: events){
            eventsWithParticipantsDto.add(eventMapper.toEventWithParticipantsDto(event));
        }

        EventServiceClass.EventsWithParticipantsList response = EventServiceClass.EventsWithParticipantsList.newBuilder()
                .addAllEvents(eventsWithParticipantsDto)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEventsWithoutParticipantsList (EventServiceClass.EventRequest request,
                                               StreamObserver<EventServiceClass.EventsWithoutParticipantsList> responseStreamObserver){

        eventService.markPastEventsAsCompleted();

        List<Event> events = eventRepository.findAllJoinParticipants();
        List<EventServiceClass.EventWithoutParticipantsDto> eventsWithoutParticipantsDto = new ArrayList<>();

        for(Event event : events){
            //valida si el usuario que hizo el request forma parte del evento
            boolean joined = event.getParticipants().stream().anyMatch(p-> p.getId().equals(request.getId()));

            eventsWithoutParticipantsDto.add(eventMapper.toEventWithoutParticipantsDto(event, joined));
        }

        EventServiceClass.EventsWithoutParticipantsList response = EventServiceClass.EventsWithoutParticipantsList.
                newBuilder()
                .addAllEvents(eventsWithoutParticipantsDto)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

}
