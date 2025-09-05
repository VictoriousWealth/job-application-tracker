package com.nick.job_application_tracker.config.factory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class ApiResponseFactory {

    /**
     * Builds a success response with a JSON schema reference to the given DTO class.
     *
     * @param dtoClass    The response DTO class.
     * @param description The description of the response.
     * @return ApiResponse with JSON content schema.
     */
    public static ApiResponse successResponse(Class<?> dtoClass, String description) {
        String schemaRef = "#/components/schemas/" + dtoClass.getSimpleName();
        return buildResponse(description, schemaRef, null);
    }

    /**
     * Builds a standard error response using the ErrorResponseDTO schema and a dynamic UTC timestamp.
     *
     * @param description The response description.
     * @param statusCode  HTTP status code.
     * @param error       Error label (e.g., "Bad Request").
     * @param message     Human-readable message.
     * @param path        Request path.
     * @return ApiResponse with error example and schema.
     */
    public static ApiResponse errorResponse(String description, int statusCode, String error, String message, String path) {
        Map<String, Object> exampleBody = Map.of(
            "status", statusCode,
            "error", error,
            "message", message,
            "path", path,
            "timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );

        Example example = new Example().value(exampleBody);
        return buildResponse(description, "#/components/schemas/ErrorResponseDTO", example);
    }

    /**
     * Helper to build a response with a given schema ref and optional example.
     */
    private static ApiResponse buildResponse(String description, String schemaRef, Example example) {
        Schema<?> schema = new Schema<>().$ref(schemaRef);
        MediaType mediaType = new MediaType().schema(schema);

        if (example != null) {
            mediaType.addExamples("example", example);
        }

        Content content = new Content().addMediaType("application/json", mediaType);
        return new ApiResponse().description(description).content(content);
    }
}
