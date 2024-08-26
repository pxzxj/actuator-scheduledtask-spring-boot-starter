package io.github.pxzxj;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.time.LocalDateTime;
import java.util.List;

@Endpoint(id = "schedule")
public class ScheduleEndpoint {

    private final TaskExecutionRepository taskExecutionRepository;

    public ScheduleEndpoint(TaskExecutionRepository taskExecutionRepository) {
        this.taskExecutionRepository = taskExecutionRepository;
    }

    @ReadOperation
    public List<TaskExecution> query(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit) {
        return taskExecutionRepository.query(method, startTime, endTime, page, limit);
    }


}
