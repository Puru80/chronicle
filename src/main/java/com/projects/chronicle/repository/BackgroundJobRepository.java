package com.projects.chronicle.repository;


import com.projects.chronicle.model.BackgroundJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BackgroundJobRepository extends JpaRepository<BackgroundJob, UUID> {

    @Query(
        value = """
        SELECT job_id\s
        FROM background_jobs\s
        WHERE status IN ('QUEUED', 'RETRYING')\s
          AND run_at <= NOW()\s
        ORDER BY run_at ASC\s
        FOR UPDATE SKIP LOCKED\s
        LIMIT 1
       \s""",
        nativeQuery = true
    )
    Optional<UUID> findNextJobToLock();

    // Find all distinct worker IDs that currently hold a lock
    @Query("SELECT DISTINCT j.lockedBy FROM BackgroundJob j WHERE j.status = 'RUNNING' AND j.lockedBy IS NOT NULL")
    List<String> findActiveWorkers();

    // Rescue the orphaned jobs by stripping the lock and resetting status
    @Modifying
    @Query("""
        UPDATE BackgroundJob j\s
        SET j.status = 'QUEUED', j.lockedBy = NULL, j.lockedAt = NULL\s
        WHERE j.lockedBy = :workerId AND j.status = 'RUNNING'
       \s""")
    int requeueOrphanedJobs(@Param("workerId") String workerId);
}
