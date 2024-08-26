package io.github.pxzxj;

import java.util.List;

public interface ScheduledTaskExecutionRepository {

    void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender);

    void finish(ScheduledTaskExecution scheduledTaskExecution);

    List<ScheduledTaskExecution> query(String method, String startTime, String endTime, int page, int limit);

}
