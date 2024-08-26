package io.github.pxzxj;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskExecutionRepository {

    void save(TaskExecution taskExecution);

    List<TaskExecution> page(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit);

}
