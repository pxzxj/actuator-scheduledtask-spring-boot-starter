package io.github.pxzxj;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.lang.Nullable;

import java.util.List;

@Endpoint(id = "scheduledexecutions")
public class ScheduledTaskExecutionsEndpoint {

    private final ScheduledTaskExecutionRepository scheduledTaskExecutionRepository;

    public ScheduledTaskExecutionsEndpoint(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        this.scheduledTaskExecutionRepository = scheduledTaskExecutionRepository;
    }

    @ReadOperation
    public List<ScheduledTaskExecution> query(@Nullable String method, @Nullable String startTime, @Nullable String endTime, int page, int limit) {
        return scheduledTaskExecutionRepository.query(method, startTime, endTime, page, limit);
    }

    @ReadOperation
    public String log(@Selector Long id) {
        return null;
    }

}
