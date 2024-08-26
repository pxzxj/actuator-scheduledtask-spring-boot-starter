package io.github.pxzxj;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

    private final ScheduledProperties scheduledProperties;

    private final JdbcTemplate jdbcTemplate;

    public JdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate) {
        this.scheduledProperties = scheduledProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender) {

    }

    @Override
    public void finish(ScheduledTaskExecution scheduledTaskExecution) {

    }

    @Override
    public List<ScheduledTaskExecution> query(String method, String startTime, String endTime, int page, int limit) {
        return null;
    }

}
