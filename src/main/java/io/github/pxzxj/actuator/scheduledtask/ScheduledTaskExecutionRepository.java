package io.github.pxzxj.actuator.scheduledtask;

public interface ScheduledTaskExecutionRepository {

    void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender);

    void finish(ScheduledTaskExecution scheduledTaskExecution);

    Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size);

    String log(Long id);

}
