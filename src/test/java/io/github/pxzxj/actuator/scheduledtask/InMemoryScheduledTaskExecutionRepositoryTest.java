package io.github.pxzxj.actuator.scheduledtask;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class InMemoryScheduledTaskExecutionRepositoryTest {

    @Test
    public void concurrentStartTest() throws InterruptedException {
        InMemoryScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository = new InMemoryScheduledTaskExecutionRepository(new ScheduledProperties());
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
            Thread thread = new Thread(() -> {
                inMemoryScheduledTaskExecutionRepository.start(scheduledTaskExecution, new ByteArrayOutputStream());
                latch.countDown();
            });
            thread.start();
        }
        latch.await();
        List<?> list = (List<?>) ReflectionTestUtils.getField(inMemoryScheduledTaskExecutionRepository, "scheduledTaskExecutions");
        ConcurrentHashMap<?, ?> concurrentHashMap = (ConcurrentHashMap<?, ?>) ReflectionTestUtils.getField(inMemoryScheduledTaskExecutionRepository, "executingTaskLogs");
        Assert.assertEquals(100, list.size());
        Assert.assertEquals(100, concurrentHashMap.size());
    }

    @Test
    public void memorySizeLimitTest() {
        ScheduledProperties scheduledProperties = new ScheduledProperties();
        scheduledProperties.setMemorySizeLimit(50);
        InMemoryScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository = new InMemoryScheduledTaskExecutionRepository(scheduledProperties);
        for (int i = 0; i < 100; i++) {
            ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
            inMemoryScheduledTaskExecutionRepository.start(scheduledTaskExecution, new ByteArrayOutputStream());
        }
        List<?> list = (List<?>) ReflectionTestUtils.getField(inMemoryScheduledTaskExecutionRepository, "scheduledTaskExecutions");
        Assert.assertEquals(50, list.size());
    }

    @Test
    public void concurrentStartFinishTest() throws InterruptedException {
        InMemoryScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository = new InMemoryScheduledTaskExecutionRepository(new ScheduledProperties());
        List<ScheduledTaskExecution> startList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
            startList.add(scheduledTaskExecution);
            inMemoryScheduledTaskExecutionRepository.start(scheduledTaskExecution, new ByteArrayOutputStream());
        }
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Thread thread;
            if (i < 50) {
                int finalI = i;
                thread = new Thread(() -> {
                    inMemoryScheduledTaskExecutionRepository.finish(startList.get(finalI));
                    latch.countDown();
                });

            } else {
                ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
                thread = new Thread(() -> {
                    inMemoryScheduledTaskExecutionRepository.start(scheduledTaskExecution, new ByteArrayOutputStream());
                    latch.countDown();
                });
            }

            thread.start();
        }
        latch.await();
        List<?> list = (List<?>) ReflectionTestUtils.getField(inMemoryScheduledTaskExecutionRepository, "scheduledTaskExecutions");
        ConcurrentHashMap<?, ?> concurrentHashMap = (ConcurrentHashMap<?, ?>) ReflectionTestUtils.getField(inMemoryScheduledTaskExecutionRepository, "executingTaskLogs");
        Assert.assertEquals(100, list.size());
        Assert.assertEquals(50, concurrentHashMap.size());
    }

}