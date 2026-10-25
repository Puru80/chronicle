package com.projects.chronicle.repository;


import com.projects.chronicle.model.BackgroundJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
