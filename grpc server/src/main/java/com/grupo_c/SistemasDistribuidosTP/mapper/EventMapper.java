package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.service.EventServiceClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class EventMapper {

    public EventServiceClass.EventWithoutParticipantsDto toDto (Event e){

        return EventServiceClass.EventWithoutParticipantsDto.newBuilder()
                .setId(e.getId())
                .setName(e.getName())
                .setDescription(e.getDescription())
                .setDate(toTimestamp(e.getDate()))
                .setIsCompleted(e.getIsCompleted())
                .build();
    }

    public Event toEntity(EventServiceClass.EventDto dto){
        Event e = new Event();

        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setDate(toLocalDateTime(dto.getDate()));
        e.setIsCompleted(false);

        return e;
    }

    // convierte timestamp a localdatetime
    public LocalDateTime toLocalDateTime(com.google.protobuf.Timestamp t){
        Instant instant = Instant.ofEpochSecond(t.getSeconds(), t.getNanos());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // convierte localdatetime a timestamp
    public com.google.protobuf.Timestamp toTimestamp(LocalDateTime t){
        Instant instant = t.toInstant(ZoneOffset.UTC);

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }


}
