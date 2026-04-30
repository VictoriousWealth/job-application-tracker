package com.nick.job_application_tracker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService.CalendarExportPayload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Calendar", description = "Calendar export and event integration endpoints")
public class CalendarController {

    private final CalendarIntegrationService calendarIntegrationService;

    public CalendarController(CalendarIntegrationService calendarIntegrationService) {
        this.calendarIntegrationService = calendarIntegrationService;
    }

    @GetMapping("/events")
    @Operation(summary = "List upcoming calendar events for reminders and scheduled communications")
    public ResponseEntity<List<CalendarEventResponseDTO>> getEvents(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(calendarIntegrationService.getEvents(from, to));
    }

    @GetMapping(value = "/events.ics", produces = "text/calendar")
    @Operation(summary = "Export upcoming reminders and scheduled communications as an ICS calendar")
    public ResponseEntity<byte[]> exportCalendar(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        CalendarExportPayload payload = calendarIntegrationService.exportCalendar(from, to);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.filename() + "\"")
            .contentType(payload.contentType())
            .body(payload.content());
    }
}
