package io.github.pxzxj.actuator.scheduledtask;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.Map;

@Endpoint(id = "scheduledtaskexecutions")
public class ScheduledTaskExecutionsEndpoint {

    private final ScheduledTaskExecutionRepository scheduledTaskExecutionRepository;

    public ScheduledTaskExecutionsEndpoint(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        this.scheduledTaskExecutionRepository = scheduledTaskExecutionRepository;
    }

    @ReadOperation
    public Page<ScheduledTaskExecution> query(@Nullable String methodName, @Nullable String startTimeStart, @Nullable String startTimeEnd,
                                              @Nullable String endTimeStart, @Nullable String endTimeEnd, int page, int size) {
        return scheduledTaskExecutionRepository.page(methodName, startTimeStart, startTimeEnd, endTimeStart, endTimeEnd, page, size);
    }

    @ReadOperation
    public Map<String, Object> log(@Selector Long id) {
        String log = scheduledTaskExecutionRepository.log(id);
        return Collections.singletonMap("log", log);
    }

}
