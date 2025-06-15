package com.nick.job_application_tracker.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
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
                .description("REST API for managing job applications, timelines, and communications."))
            .components(new Components()
                .addSchemas("ErrorResponseDTO", new Schema<>()  // <-- Swagger schema ref
                    .$ref("#/components/schemas/ErrorResponseDTO")));
    }

    @Bean
    public OpenApiCustomizer globalErrorResponseCustomizer() {
        return openApi -> {
            // Define the content for the error response
            Content errorContent = new Content().addMediaType("application/json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponseDTO")));

            ApiResponse badRequest = new ApiResponse()
                .description("Bad Request")
                .content(errorContent);

            ApiResponse internalError = new ApiResponse()
                .description("Internal Server Error")
                .content(errorContent);

            // Apply these to every operation
            openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses().addApiResponse("400", badRequest);
                    operation.getResponses().addApiResponse("500", internalError);
                })
            );
        };
    }
}
