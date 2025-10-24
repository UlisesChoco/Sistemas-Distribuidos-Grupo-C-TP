package com.empujecomunitario.rest_server.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empujecomunitario.rest_server.entity.EventReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Reportes de Eventos")
public class EventReportController {

    @Operation(summary = "Generar informe de eventos", description = "Retorna participación en eventos aplicando filtros")
    @GetMapping("/report")
    public ResponseEntity<List<EventReportResponse>> generateEventReport(
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Boolean repartoDonaciones) {

        // Por ahora devolvemos datos de ejemplo
        List<EventReportResponse> report = List.of(
                new EventReportResponse(1L, "Evento Caridad", "Recolección de alimentos",
                        "2024-10-15", true, "Juan Pérez"),
                new EventReportResponse(2L, "Maratón Solidaria", "Carrera por donaciones",
                        "2024-10-20", false, "María García"));

        return ResponseEntity.ok(report);
    }
}
