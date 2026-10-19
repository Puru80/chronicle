package com.projects.chronicle.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ScheduleJobRequest(
    @NotBlank(message = "Job Type is required" )
    String jopType,

    @NotNull(message = "Payload cannot be null")
    JsonNode payload,

    // Optional: If null, the job executes immediately.
    // If provided, the system defers execution until this timestamp.
    OffsetDateTime runAt
) {
}
