package com.nick.job_application_tracker.config;

import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.dto.special.ErrorResponseDTO;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Job Application Tracker API")
                .version("1.0")
                .description("REST API for managing job applications, timelines, and communications."));
    }

    @Bean
    public OpenApiCustomizer methodBasedResponseCustomizer() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) ->
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {

                switch (httpMethod.name()) {
                    case "GET" -> {
                        operation.getResponses().addApiResponse("200", successResponse(ApplicationTimelineResponseDTO.class, "Successful GET"));
                        operation.getResponses().addApiResponse("404", errorResponse(404, "Not Found", "Timeline not found", path));
                    }
                    case "POST" -> {
                        operation.getResponses().addApiResponse("201", successResponse(ApplicationTimelineResponseDTO.class, "Resource created"));
                        operation.getResponses().addApiResponse("400", errorResponse(400, "Bad Request", "Invalid input", path));
                        operation.getResponses().addApiResponse("409", errorResponse(409, "Conflict", "Duplicate entry", path));
                    }
                    case "PUT" -> {
                        operation.getResponses().addApiResponse("200", successResponse(ApplicationTimelineResponseDTO.class, "Resource updated"));
                        operation.getResponses().addApiResponse("400", errorResponse(400, "Bad Request", "Invalid update payload", path));
                        operation.getResponses().addApiResponse("404", errorResponse(404, "Not Found", "Resource to update not found", path));
                        operation.getResponses().addApiResponse("409", errorResponse(409, "Conflict", "Update conflict", path));
                    }
                    case "PATCH" -> {
                        operation.getResponses().addApiResponse("200", successResponse(ApplicationTimelineResponseDTO.class, "Resource partially updated"));
                        operation.getResponses().addApiResponse("400", errorResponse(400, "Bad Request", "Invalid patch payload", path));
                        operation.getResponses().addApiResponse("404", errorResponse(404, "Not Found", "Resource not found", path));
                    }
                    case "DELETE" -> {
                        operation.getResponses().addApiResponse("204", new ApiResponse().description("No Content"));
                        operation.getResponses().addApiResponse("404", errorResponse(404, "Not Found", "Resource not found", path));
                    }
                    case "OPTIONS" -> {
                        operation.getResponses().addApiResponse("200", new ApiResponse().description("Supported methods returned in Allow header"));
                    }
                    case "HEAD" -> {
                        operation.getResponses().addApiResponse("200", new ApiResponse().description("Headers returned without body"));
                        operation.getResponses().addApiResponse("404", errorResponse(404, "Not Found", "Resource not found", path));
                    }
                    case "TRACE" -> {
                        operation.getResponses().addApiResponse("200", new ApiResponse().description("Trace response with request echo"));
                    }
                    default -> {
                        operation.getResponses().addApiResponse("501", new ApiResponse().description("Not Implemented"));
                    }
                }

                // Common error responses for all methods
                operation.getResponses().addApiResponse("401", errorResponse(401, "Unauthorized", "Missing or invalid credentials", path));
                operation.getResponses().addApiResponse("403", errorResponse(403, "Forbidden", "Access denied", path));
                operation.getResponses().addApiResponse("422", errorResponse(422, "Unprocessable Entity", "Validation failed", path));
                operation.getResponses().addApiResponse("500", errorResponse(500, "Internal Server Error", "Unexpected failure", path));
                operation.getResponses().addApiResponse("503", errorResponse(503, "Service Unavailable", "Server overloaded or under maintenance", path));
            })
        );
    }

    private ApiResponse successResponse(Class<?> dtoClass, String description) {
        Schema<?> schema = new Schema<>().$ref("#/components/schemas/" + dtoClass.getSimpleName());
        MediaType mediaType = new MediaType().schema(schema);
        Content content = new Content().addMediaType("application/json", mediaType);
        return new ApiResponse().description(description).content(content);
    }

    private ApiResponse errorResponse(int status, String error, String message, String path) {
        Schema<?> schema = new Schema<>().$ref("#/components/schemas/" + ErrorResponseDTO.class.getSimpleName());
        MediaType mediaType = new MediaType().schema(schema).addExamples("example", new Example().value(
            Map.of(
                "status", status,
                "error", error,
                "message", message,
                "path", path,
                "timestamp", "2025-06-15T12:34:56.123Z"
            )
        ));
        Content content = new Content().addMediaType("application/json", mediaType);
        return new ApiResponse().description(error).content(content);
    }
}
