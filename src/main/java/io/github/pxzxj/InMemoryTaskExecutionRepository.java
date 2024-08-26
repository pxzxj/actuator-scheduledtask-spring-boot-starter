package io.github.pxzxj;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTaskExecutionRepository implements TaskExecutionRepository {

    private final ScheduleProperties actuatorScheduleProperties;

    public InMemoryTaskExecutionRepository(ScheduleProperties actuatorScheduleProperties) {
        this.actuatorScheduleProperties = actuatorScheduleProperties;
    }

    @Override
    public void save(TaskExecution taskExecution) {

    }

    @Override
    public List<TaskExecution> page(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit) {
        return null;
    }
}
