package com.projects.chronicle.service;

import com.projects.chronicle.dto.ScheduleJobRequest;
import com.projects.chronicle.model.BackgroundJob;
import com.projects.chronicle.repository.BackgroundJobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class JobCoordinatorService {

    private final BackgroundJobRepository backgroundJobRepository;

    public JobCoordinatorService(BackgroundJobRepository backgroundJobRepository){
        this.backgroundJobRepository = backgroundJobRepository;
    }

    @Transactional
    public UUID enqueueJob(ScheduleJobRequest scheduleJobRequest){
        BackgroundJob job = new BackgroundJob();
        job.setJobType(scheduleJobRequest.jopType());
        job.setPayload(scheduleJobRequest.payload());

        job.setRunAt(scheduleJobRequest.runAt() != null ? scheduleJobRequest.runAt() : OffsetDateTime.now());

        BackgroundJob savedJob = backgroundJobRepository.save(job);

        return savedJob.getJobId();
    }
}
