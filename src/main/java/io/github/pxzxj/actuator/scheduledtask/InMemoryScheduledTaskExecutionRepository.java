package io.github.pxzxj.actuator.scheduledtask;

import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

    private final LinkedList<ScheduledTaskExecution> scheduledTaskExecutions;

    private final AtomicLong index = new AtomicLong();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<Long, ByteArrayOutputStream> executingTaskLogs = new ConcurrentHashMap<>();

    public InMemoryScheduledTaskExecutionRepository(ScheduledProperties actuatorScheduledProperties) {
        int memorySizeLimit = actuatorScheduledProperties.getMemorySizeLimit();
        scheduledTaskExecutions = new LinkedList<ScheduledTaskExecution>() {
            @Override
            public void addFirst(ScheduledTaskExecution scheduledTaskExecution) {
                if(scheduledTaskExecutions.size() >= memorySizeLimit) {
                    scheduledTaskExecutions.removeLast();
                }
                super.addFirst(scheduledTaskExecution);
            }
        };
    }

    @Override
    public void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream) {
        scheduledTaskExecution.setId(index.incrementAndGet());
        executingTaskLogs.put(scheduledTaskExecution.getId(), byteArrayOutputStream);
        readWriteLock.writeLock().lock();
        scheduledTaskExecutions.addFirst(scheduledTaskExecution);
        readWriteLock.writeLock().unlock();
    }

    @Override
    public void finish(ScheduledTaskExecution scheduledTaskExecution) {
        executingTaskLogs.remove(scheduledTaskExecution.getId());
    }

    @Override
    public Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size) {
        readWriteLock.readLock().lock();
        List<ScheduledTaskExecution> list = new ArrayList<>(scheduledTaskExecutions);
        readWriteLock.readLock().unlock();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Iterator<ScheduledTaskExecution> iterator = list.iterator();
        while (iterator.hasNext()) {
            ScheduledTaskExecution scheduledTaskExecution = iterator.next();
            boolean remove = false;
            if (StringUtils.hasText(methodName) && !scheduledTaskExecution.getMethodName().contains(methodName)) {
                remove = true;
            } else if (StringUtils.hasText(startTimeStart) && LocalDateTime.parse(startTimeStart, dateTimeFormatter).isAfter(scheduledTaskExecution.getStartTime())) {
                remove = true;
            } else if (StringUtils.hasText(startTimeEnd) && LocalDateTime.parse(startTimeEnd, dateTimeFormatter).isBefore(scheduledTaskExecution.getStartTime())) {
                remove = true;
            } else if (StringUtils.hasText(endTimeStart) && scheduledTaskExecution.getEndTime() != null && LocalDateTime.parse(endTimeStart, dateTimeFormatter).isAfter(scheduledTaskExecution.getEndTime())) {
                remove = true;
            } else if (StringUtils.hasText(endTimeEnd) && scheduledTaskExecution.getEndTime() != null && LocalDateTime.parse(endTimeEnd, dateTimeFormatter).isBefore(scheduledTaskExecution.getEndTime())) {
                remove = true;
            }
            if (remove) {
                iterator.remove();
            }
        }
        return Page.of(list, page, size);
    }

    @Override
    public String log(Long id) {
        ByteArrayOutputStream byteArrayOutputStream = executingTaskLogs.get(id);
        if (byteArrayOutputStream != null) {
            return byteArrayOutputStream.toString();
        }
        readWriteLock.readLock().lock();
        String log = scheduledTaskExecutions.stream().filter(ext -> ext.getId().equals(id)).findFirst().map(ScheduledTaskExecution::getLog).orElse(null);
        readWriteLock.readLock().unlock();
        return log;
    }
}
