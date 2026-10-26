package com.projects.chronicle.service;

import com.projects.chronicle.model.BackgroundJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JobExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionEngine.class);
    private final JobWorkerService jobWorkerService;
    private final HandlerRegistry jobHandlerRegistry;

    public JobExecutionEngine(JobWorkerService jobWorkerService, HandlerRegistry handlerRegistry){
        this.jobWorkerService = jobWorkerService;
        this.jobHandlerRegistry = handlerRegistry;
    }

    @Scheduled(fixedDelay = 1000)
    public void pollAndExecute(){
        Optional<BackgroundJob> claimedJobOpt = jobWorkerService.claimNext();

        if(claimedJobOpt.isEmpty()){
            return; // No jobs available, sleep until next poll
        }

        BackgroundJob job = claimedJobOpt.get();
        log.info("Claimed job: {} of type: {}", job.getJobId(), job.getJobType());

        try{
            // ==========================================
            // EXECUTE DOMAIN LOGIC HERE (NO DB TRANSACTION)
            // ==========================================
            JobHandler handler = jobHandlerRegistry.get(job.getJobType());

            handler.execute(job.getPayload());

            jobWorkerService.markComplete(job.getJobId());
            log.info("Successfully Completed Job: {}", job.getJobId());
        } catch (Exception e) {
            log.error("Job execution failed: {}", job.getJobId(), e);
            // In a production system, we'd extract the root cause stack trace here
            jobWorkerService.markFailed(job.getJobId(), e.getMessage());
        }
    }

    /*public void simulateJobExecution(BackgroundJob job) throws InterruptedException{
        // We will replace this with a Strategy Pattern interface later
        if ("EOD_RECONCILIATION".equals(job.getJobType())) {
            log.info("Processing reconciliation for payload: {}", job.getPayload().toString());
            Thread.sleep(2000); // Simulate network I/O

            // Deliberately fail 10% of the time to test our retry state machine
            if (Math.random() < 0.1) {
                throw new RuntimeException("Simulated network timeout connecting to Broker API");
            }
        }
    }*/
}
