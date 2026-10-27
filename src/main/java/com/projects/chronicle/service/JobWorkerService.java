package com.projects.chronicle.service;

import com.projects.chronicle.enums.JobStatus;
import com.projects.chronicle.model.BackgroundJob;
import com.projects.chronicle.repository.BackgroundJobRepository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobWorkerService {

    private final BackgroundJobRepository backgroundJobRepository;
    private final StringRedisTemplate redisTemplate;
    private final String workerId;

    public JobWorkerService(BackgroundJobRepository backgroundJobRepository, StringRedisTemplate stringRedisTemplate){
        this.backgroundJobRepository = backgroundJobRepository;
        this.redisTemplate = stringRedisTemplate;
        // Generate a unique identity for this worker instance on boot
        this.workerId = "worker-" + UUID.randomUUID();
    }

    //Broadcast Heartbeat every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void emitHeartbeat(){
        String key = "worker:heartbeat:" + workerId;
        redisTemplate.opsForValue().set(key, "alive", Duration.ofSeconds(15));
    }

    // REQUIRES_NEW ensures this claim happens in its own isolated transaction
    // and commits immediately upon returning.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<BackgroundJob> claimNext(){
        return backgroundJobRepository.findNextJobToLock()
            .flatMap(backgroundJobRepository :: findById)
            .map(job -> {
                job.setStatus(JobStatus.RUNNING);
                job.setLockedBy(workerId);
                job.setLockedAt(OffsetDateTime.now());
                return backgroundJobRepository.save(job);
            });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markComplete(UUID jobId){
        backgroundJobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(JobStatus.COMPLETED);
            job.setLockedBy(null);
            job.setLockedAt(null);
            backgroundJobRepository.save(job);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID jobId, String errorMessage){
        backgroundJobRepository.findById(jobId).ifPresent(job -> {
            job.setRetriesCount(job.getRetriesCount() + 1);
            job.setLastError(errorMessage);
            job.setLockedBy(null);
            job.setLockedAt(null);

            if(job.getRetriesCount() > job.getMaxRetries()){
                job.setStatus(JobStatus.FAILED);
            } else{
                job.setStatus(JobStatus.RETRYING);
                // Exponential backoff: base_delay * 2^retries
                long delaySeconds = (long) Math.pow(2, job.getRetriesCount() * 15);
                job.setRunAt(OffsetDateTime.now().plusSeconds(delaySeconds));
            }

            backgroundJobRepository.save(job);
        });
    }

}
