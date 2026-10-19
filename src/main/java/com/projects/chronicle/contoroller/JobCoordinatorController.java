package com.projects.chronicle.contoroller;

import com.projects.chronicle.dto.ScheduleJobRequest;
import com.projects.chronicle.service.JobCoordinatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job")
public class JobCoordinatorController {

    private final JobCoordinatorService jobCoordinatorService;

    public JobCoordinatorController(JobCoordinatorService jobCoordinatorService){
        this.jobCoordinatorService = jobCoordinatorService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> scheduleJob(@Valid @RequestBody ScheduleJobRequest scheduleJobRequest){
        UUID jobId = jobCoordinatorService.enqueueJob(scheduleJobRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("job_id", jobId));
    }
}
