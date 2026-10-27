package com.projects.chronicle.service;

import com.projects.chronicle.model.BackgroundJob;
import com.projects.chronicle.repository.BackgroundJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobReaperService {

    private final Logger log = LoggerFactory.getLogger(JobReaperService.class);
    private final BackgroundJobRepository repository;
    private final StringRedisTemplate redisTemplate;

    public JobReaperService(BackgroundJobRepository backgroundJobRepository, StringRedisTemplate stringRedisTemplate){
        this.repository = backgroundJobRepository;
        this.redisTemplate = stringRedisTemplate;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void reapDeadWorker(){
        List<String> activeWorkers = repository.findActiveWorkers();

        for(String workerId: activeWorkers){
            String key = "worker:heartbeat:" + workerId;
            Boolean isAlive = redisTemplate.hasKey(key);

            if(Boolean.FALSE.equals(isAlive)){
                log.warn("Worker {} missed heartbeat. Reclaiming jobs.", workerId);
                int rescuedCount = repository.requeueOrphanedJobs(workerId);
                log.info("Successfully rescued {} jobs from dead worker {}", rescuedCount, workerId);
            }
        }
    }
}
