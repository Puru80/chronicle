package com.projects.chronicle.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HandlerRegistry {

    private final Map<String, JobHandler> handlers;

    public HandlerRegistry(List<JobHandler> jobHandlers){
        this.handlers = jobHandlers.stream()
            .collect(Collectors.toMap(JobHandler::getJobType, Function.identity()));
    }

    public JobHandler get(String jobType){
        JobHandler handler = handlers.get(jobType);
        if (handler == null) {
            // This is a critical failure. A job was queued but the code to process it doesn't exist.
            throw new UnsupportedOperationException("No handler registered for job type: " + jobType);
        }
        return handler;
    }
}
