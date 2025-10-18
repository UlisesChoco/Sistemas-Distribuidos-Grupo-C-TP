package com.empujecomunitario.rest_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_filters")
public class EventFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String filterName;

    @Column(columnDefinition = "TEXT")
    private String criteriaJson;
}
