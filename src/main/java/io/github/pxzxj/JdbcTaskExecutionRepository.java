package io.github.pxzxj;

import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class JdbcTaskExecutionRepository implements TaskExecutionRepository {

    private final ScheduleProperties scheduleProperties;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaskExecutionRepository(ScheduleProperties scheduleProperties, JdbcTemplate jdbcTemplate) {
        this.scheduleProperties = scheduleProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void start(TaskExecution taskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender) {

    }

    @Override
    public void finish(TaskExecution taskExecution) {

    }

    @Override
    public List<TaskExecution> query(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit) {
        return null;
    }
}
