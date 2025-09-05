package com.nick.job_application_tracker.service.common;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A generic service interface that defines the core CRUD operations 
 * for any domain model in the system.
 *
 * <p>This interface enforces a standard structure across all services by requiring:
 * <ul>
 *   <li>DTO-based creation and update methods</li>
 *   <li>Detailed retrieval of single records</li>
 *   <li>Paginated listing of entities</li>
 *   <li>Support for partial updates via JSON patching</li>
 *   <li>Soft deletion with preservation of historical data</li>
 * </ul>
 *
 * @param <CreateDTO> the type used when creating a new entity
 * @param <ResponseDTO> the type used for lightweight list responses
 * @param <UpdateDTO> the type used when fully updating an entity
 * @param <DetailDTO> the type used for detailed views of a single entity
 * @param <Model> the underlying JPA model/entity type
 */
public interface ServiceInterface<
        CreateDTO,
        ResponseDTO,
        UpdateDTO,
        DetailDTO,
        Model> {

    /**
     * Creates a new entity from the provided DTO.
     *
     * @param dto the data used to create the entity
     * @return a lightweight response DTO of the created entity
     */
    ResponseDTO create(CreateDTO dto);

    /**
     * Retrieves a detailed view of an entity by its ID.
     *
     * @param id the unique identifier of the entity
     * @return a detailed DTO of the entity
     */
    DetailDTO getDetailById(UUID id);

    /**
     * Retrieves the raw model entity by its ID.
     * Useful for internal service logic or validation.
     *
     * @param id the unique identifier of the entity
     * @return the underlying model entity
     */
    Model getModelById(UUID id);

    /**
     * Retrieves a paginated list of lightweight DTOs for the current user's entities.
     *
     * @param pageable pagination and sorting information
     * @return a page of response DTOs
     */
    Page<ResponseDTO> getAll(Pageable pageable);

    /**
     * Applies a partial update (PATCH) to an entity using the provided JSON.
     * Only the fields present in the JSON node and in patchableFields (that should be whithin the Model's mapper) are updated.
     *
     * @param id the ID of the entity to patch
     * @param node the JSON node containing patch data
     * @return the updated detailed DTO
     */
    DetailDTO patchById(UUID id, JsonNode node);

    /**
     * Fully updates an entity by replacing all mutable fields.
     *
     * @param id the ID of the entity to update
     * @param dto the new values for the entity
     * @return the updated detailed DTO
     */
    DetailDTO updateById(UUID id, UpdateDTO dto);

    /**
     * Soft-deletes the entity with the given ID.
     * The entity is excluded from future queries but not permanently removed.
     *
     * @param id the ID of the entity to soft delete
     * @return a confirmation message
     */
    String deleteById(UUID id);
}
