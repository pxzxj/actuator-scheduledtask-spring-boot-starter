package io.github.pxzxj;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTaskExecutionRepository implements TaskExecutionRepository {

    private final ScheduleProperties actuatorScheduleProperties;

    private final LinkedList<TaskExecution> list;

    private final AtomicLong index = new AtomicLong();

    private final ConcurrentHashMap<Long, ByteArrayOutputStreamAppender> executingTaskLogs = new ConcurrentHashMap<>();

    public InMemoryTaskExecutionRepository(ScheduleProperties actuatorScheduleProperties) {
        this.actuatorScheduleProperties = actuatorScheduleProperties;
        int memorySizeLimit = actuatorScheduleProperties.getMemorySizeLimit();
        list = new LinkedList<TaskExecution>() {
            @Override
            public boolean add(TaskExecution taskExecution) {
                if(list.size() >= memorySizeLimit) {
                    list.removeFirst();
                }
                return super.add(taskExecution);
            }
        };
    }

    @Override
    public synchronized void start(TaskExecution taskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender) {
        taskExecution.setId(index.incrementAndGet());
        executingTaskLogs.put(taskExecution.getId(), byteArrayOutputStreamAppender);
        list.add(taskExecution);
    }

    @Override
    public void finish(TaskExecution taskExecution) {
        executingTaskLogs.remove(taskExecution.getId());
    }

    @Override
    public List<TaskExecution> query(String method, LocalDateTime startTime, LocalDateTime endTime, int page, int limit) {
        return null;
    }
}
