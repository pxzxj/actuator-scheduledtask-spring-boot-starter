package io.github.pxzxj;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskExecutionRepository {

    void start(TaskExecution taskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender);

    void finish(TaskExecution taskExecution);

    List<TaskExecution> query(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit);

}
