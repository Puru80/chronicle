package com.projects.chronicle.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.projects.chronicle.enums.JobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "background_jobs")
public class BackgroundJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "job_id", updatable = false, nullable = false)
    private UUID jobId;

    @Column(name = "queue_name", nullable = false, length = 50)
    private String queueName = "default";

    @Column(name = "job_type", nullable = false, length = 100)
    private String jobType;

    // Hibernate 6 handles this seamlessly mapping to PostgreSQL JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status = JobStatus.QUEUED;

    @Column(name = "retries_count", nullable = false)
    private int retriesCount = 0;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries = 3;

    @Column(name = "run_at", nullable = false)
    private OffsetDateTime runAt = OffsetDateTime.now();

    @Column(name = "locked_by", length = 128)
    private String lockedBy;

    @Column(name = "locked_at")
    private OffsetDateTime lockedAt;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Getters and Setters omitted for brevity
}