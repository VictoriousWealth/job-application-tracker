package com.nick.job_application_tracker.controller.common;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.nick.job_application_tracker.service.common.ServiceInterface;

import jakarta.validation.Valid;

public abstract class BaseController<
        CreateDTO,
        ResponseDTO,
        DetailDTO,
        UpdateDTO,
        Service extends ServiceInterface<CreateDTO, ResponseDTO, UpdateDTO, DetailDTO, ?>> {

    private final Service service;

    public BaseController(Service service) {
        this.service = service;
    }



    // ################################################################
    // CREATE
    // ################################################################
    @PostMapping
    public ResponseEntity<ResponseDTO> create(@Valid @RequestBody CreateDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }



    // ################################################################
    // READ
    // ################################################################

    @GetMapping("/{id}")
    public ResponseEntity<DetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(service.getDetailById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }



    // ################################################################
    // UPDATE
    // ################################################################
    @PatchMapping("/{id}")
    public ResponseEntity<DetailDTO> patchById(@PathVariable UUID id, @RequestBody JsonNode node) {
        return ResponseEntity.ok(service.patchById(id, node));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailDTO> updateById(@PathVariable UUID id, @RequestBody UpdateDTO dto) {
        return ResponseEntity.ok(service.updateById(id, dto));
    }



    // ################################################################
    // DELETE
    // ################################################################
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        return ResponseEntity.status(204).body(service.deleteById(id));
    }


}


//  CRUD
    // Create - post 
        // - creating a new record by accepting a recordCreateDto
            // - first ensures that all the required info is there 
            // - ensures all info is of the expected type (int, string, etc.)
            // - calls services to perform futher evaluation of the validity of the info
            // - if all is fine, service calls repository to create new record
                // -> returns a 201 + a shallow representation of the record as represented by recordResponseDto


    // Read - get 
        // - reading a specific record 
            // -> returns a 200 + a deep and comphrensive representation of the record as represented by recordDetailDto
                // (will try to avoid possible cycles) 
        // - reading a page of records 
            // -> returns a 200 + a shallow representation of each record as represented by recordResponseDto
    

    // Update - patch  
        // - patching a specific bits of one individual record == soft-deleting by accepting a jsonNode that has only one key delete
            // if value matches true then calls softDelete method from service
                // -> returns a 204 + a "Ghost Content" body 
        // - patching a specific bits of one individual record == reverse soft-deleting == recover by a jsonNode that has only one key delete
            // if value matches false then calls recover method from service
                // -> returns a 201 + a deep and comphrensive representation of the record as represented by recordDetailDto 
                    // (will try to avoid possible cycles) 
        // - patching specific bits of one individual record except soft-deletion or recovery by accepting a jsonNode
            // -> returns a 200 + a deep and comphrensive representation of the record as represented by recordDetailDto 
                // (will try to avoid possible cycles) 
    

    // Update - put 
        // - putting a new set of details of the record but keeping primary key the same by accepting a recordUpdateDto
            // -> returns a 201 + a shallow representation of the record as represented by recordResponseDto

    
    // Delete - delete 
        // - deletign permanently a record by accepting a primary key
            // -> returns a 204 + a "No Content" body
    
    
    // 
    // 
    // 
