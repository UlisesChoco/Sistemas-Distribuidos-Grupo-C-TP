package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.service.EventServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.EventServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImp extends EventServiceGrpc.EventServiceImplBase {

    @Override
    public void createEvent (EventServiceClass.EventDto event,
                             StreamObserver<EventServiceClass.Response2> responseStreamObserver){

        /*
        EventServiceClass.Response response = EventServiceClass.Response.newBuilder().setSucceeded(true).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
        */
    }

    @Override
    public void modifyEvent (EventServiceClass.EventWithParticipantsDto event,
                             StreamObserver<EventServiceClass.Response2> responseStreamObserver){

    }

    @Override
    public void deleteEvent (EventServiceClass.EventRequest request,
                             StreamObserver<EventServiceClass.Response2> responseStreamObserver){

    }

    @Override
    public void assignUserToEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                   StreamObserver<EventServiceClass.Response2> responseStreamObserver){

    }

    @Override
    public void deleteUserFromEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                     StreamObserver<EventServiceClass.Response2> responseStreamObserver){

    }

    @Override
    public void getEvent (EventServiceClass.EventRequest request,
                          StreamObserver<EventServiceClass.EventWithParticipantsDto> responseStreamObserver){

    }

    @Override
    public void getEventsWithParticipantsList (EventServiceClass.Empty2 request,
                                               StreamObserver<EventServiceClass.EventsWithParticipantsList> responseStreamObserver){

    }

    @Override
    public void getEventsWithoutParticipantsList (EventServiceClass.Empty2 request,
                                               StreamObserver<EventServiceClass.EventsWithoutParticipantsList> responseStreamObserver){

    }
}
