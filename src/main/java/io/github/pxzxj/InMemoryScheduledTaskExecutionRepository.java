package io.github.pxzxj;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

    private final LinkedList<ScheduledTaskExecution> list;

    private final AtomicLong index = new AtomicLong();

    private final ConcurrentHashMap<Long, ByteArrayOutputStreamAppender> executingTaskLogs = new ConcurrentHashMap<>();

    public InMemoryScheduledTaskExecutionRepository(ScheduledProperties actuatorScheduledProperties) {
        int memorySizeLimit = actuatorScheduledProperties.getMemorySizeLimit();
        list = new LinkedList<ScheduledTaskExecution>() {
            @Override
            public boolean add(ScheduledTaskExecution scheduledTaskExecution) {
                if(list.size() >= memorySizeLimit) {
                    list.removeFirst();
                }
                return super.add(scheduledTaskExecution);
            }
        };
    }

    @Override
    public synchronized void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender) {
        scheduledTaskExecution.setId(index.incrementAndGet());
        executingTaskLogs.put(scheduledTaskExecution.getId(), byteArrayOutputStreamAppender);
        list.add(scheduledTaskExecution);
    }

    @Override
    public void finish(ScheduledTaskExecution scheduledTaskExecution) {
        executingTaskLogs.remove(scheduledTaskExecution.getId());
    }

    @Override
    public List<ScheduledTaskExecution> query(String method, String startTime, String endTime, int page, int limit) {
        return null;
    }
}
