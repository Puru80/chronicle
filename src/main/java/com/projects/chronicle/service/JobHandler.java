package com.projects.chronicle.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface JobHandler {
    /**
     * Defines the routing key (e.g., 'EOD_RECONCILIATION').
     * Must perfectly match the job_type in the database.
     */
    String getJobType();

    /**
     * Contains the actual domain logic.
     * Throws an exception if the job fails, which the engine catches to trigger the retry state machine.
     */
    void execute(JsonNode payload) throws Exception;

}
