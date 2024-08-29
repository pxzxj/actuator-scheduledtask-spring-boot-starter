package io.github.pxzxj.actuator.scheduledtask;

import java.io.ByteArrayOutputStream;

public interface ScheduledTaskExecutionRepository {

    /**
     * implementations must invoke scheduledTaskExecution.setId
     * @param scheduledTaskExecution
     * @param byteArrayOutputStream
     */
    void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream);

    void finish(ScheduledTaskExecution scheduledTaskExecution);

    Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size);

    String log(Long id);

}
