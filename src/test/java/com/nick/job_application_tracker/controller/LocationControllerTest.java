package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.service.implementation.LocationService;

@WebMvcTest(
    controllers = LocationController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class LocationControllerTest {

    private static final UUID LOCATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService service;

    @Test
    void getAllReturnsLocations() throws Exception {
        LocationResponseDTO response = new LocationResponseDTO();
        response.setId(LOCATION_ID);
        response.setCity("London");
        response.setCountry("United Kingdom");

        when(service.getAllLocations()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/locations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(LOCATION_ID.toString()))
            .andExpect(jsonPath("$[0].city").value("London"));
    }

    @Test
    void getByIdReturnsLocationDetail() throws Exception {
        LocationDetailDTO detail = new LocationDetailDTO();
        detail.setId(LOCATION_ID);
        detail.setCity("Manchester");
        detail.setCountry("United Kingdom");

        when(service.getLocationById(LOCATION_ID)).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/api/locations/{id}", LOCATION_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Manchester"));
    }

    @Test
    void createReturnsCreatedLocation() throws Exception {
        LocationCreateDTO request = new LocationCreateDTO();
        request.setCity("Dublin");
        request.setCountry("Ireland");

        LocationResponseDTO response = new LocationResponseDTO();
        response.setId(LOCATION_ID);
        response.setCity(request.getCity());
        response.setCountry(request.getCountry());

        when(service.createLocation(any(LocationCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.country").value("Ireland"));
    }

    @Test
    void updateReturnsUpdatedLocation() throws Exception {
        LocationUpdateDTO request = new LocationUpdateDTO();
        request.setCity("Berlin");
        request.setCountry("Germany");

        LocationDetailDTO response = new LocationDetailDTO();
        response.setId(LOCATION_ID);
        response.setCity("Berlin");
        response.setCountry("Germany");

        when(service.updateLocation(any(UUID.class), any(LocationUpdateDTO.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/locations/{id}", LOCATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Berlin"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(service).deleteLocation(LOCATION_ID);

        mockMvc.perform(delete("/api/locations/{id}", LOCATION_ID))
            .andExpect(status().isNoContent());
    }
}
