package io.github.pxzxj.actuator.scheduledtask;

import java.io.ByteArrayOutputStream;

public interface ScheduledTaskExecutionRepository {

    /**
     *
     * @param scheduledTaskExecution
     * @param byteArrayOutputStream
     * @return true if start success
     */
    boolean start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream);

    void finish(ScheduledTaskExecution scheduledTaskExecution);

    Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size);

    String log(Long id);

}
