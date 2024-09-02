package io.github.pxzxj.actuator.scheduledtask;

import java.io.ByteArrayOutputStream;

public interface ScheduledTaskExecutionRepository {

    /**
     * Persists the given {@link ScheduledTaskExecution} object into the underlying storage system.
     * After successful persistence, it invokes the {@link ScheduledTaskExecution#setId(Long)} method to
     * set the unique identifier of the persisted task execution. Additionally, this method temporarily
     * stores the provided {@link ByteArrayOutputStream} for future use or processing.
     *
     * @param scheduledTaskExecution the {@link ScheduledTaskExecution} instance to be persisted.
     * @param byteArrayOutputStream the ByteArrayOutputStream for log of ScheduledTaskExecution instance
     * @return a boolean indicating whether the persistence operation was successful.
     */
    boolean start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream);

    /**
     * Updates the underlying storage system with the log, exception, and other relevant details of the completed task.
     *
     *
     * @param scheduledTaskExecution The instance of {@link ScheduledTaskExecution} that represents the completed task. This object
     *                               encapsulates all the necessary details about the task, including its state, logs, and exceptions.
     */
    void finish(ScheduledTaskExecution scheduledTaskExecution);

    /**
     * Queries and retrieves a paginated list of {@link ScheduledTaskExecution} instances based on the specified filter criteria.
     *
     * @param methodName The name of the method executed by the scheduled task, used as a filter condition.
     * @param startTimeStart The start of the range for the start time of the scheduled task executions. Use null or empty string for no lower bound.
     * @param startTimeEnd The end of the range for the start time of the scheduled task executions. Use null or empty string for no upper bound.
     * @param endTimeStart The start of the range for the end time of the scheduled task executions. Use null or empty string for no lower bound.
     * @param endTimeEnd The end of the range for the end time of the scheduled task executions. Use null or empty string for no upper bound.
     * @param page The page number to retrieve, starting from 0.
     * @param size The number of records to retrieve per page.
     * @return A {@link Page} object containing the requested page of {@link ScheduledTaskExecution} records.
     */
    Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size);

    /**
     * query log of ScheduledTaskExecution instance
     * @param id ScheduledTaskExecution id
     * @return log content
     */
    String log(Long id);

}
