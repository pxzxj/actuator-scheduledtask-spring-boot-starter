package io.github.pxzxj.actuator.scheduledtask;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.*;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Endpoint(id = "scheduledtaskdefinitions")
public class ScheduledTaskDefinitionsEndpoint implements InitializingBean {

    private final Collection<ScheduledTaskHolder> scheduledTaskHolders;

    private final List<ScheduledTaskDefinition> scheduledTaskDefinitions = new CopyOnWriteArrayList<>();

    private final AtomicInteger index = new AtomicInteger();

    public ScheduledTaskDefinitionsEndpoint(Collection<ScheduledTaskHolder> scheduledTaskHolders) {
        this.scheduledTaskHolders = scheduledTaskHolders;
    }

    @ReadOperation
    public Page<ScheduledTaskDefinition> page(@Nullable String methodName, int page, int size) {
        List<ScheduledTaskDefinition> list;
        if (StringUtils.hasText(methodName)) {
            list = getScheduledTaskHolders().stream().filter(def -> def.getMethodName().contains(methodName)).collect(Collectors.toList());
        } else {
            list = new ArrayList<>(getScheduledTaskHolders());
        }
        return Page.of(list, page, size);
    }

    @WriteOperation
    public void operate(@Selector Integer id, Action action) {
        if (Action.CANCEL.equals(action)) {
            cancel(id);
        } else {
            execute(id);
        }
    }

    private void cancel(Integer id) {
        ScheduledTaskDefinition scheduledTaskDefinition = getScheduledTaskHolders().stream()
                .filter(def -> def.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("scheduledTaskDefinition not found for id " + id));
        scheduledTaskDefinition.setState(ScheduledTaskDefinition.State.CANCELED);
        scheduledTaskDefinition.getScheduledTask().cancel();
    }

    private void execute(Integer id) {
        ScheduledTaskDefinition scheduledTaskDefinition = getScheduledTaskHolders().stream()
                .filter(def -> def.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("scheduledTaskDefinition not found for id " + id));
        Thread thread = new Thread(scheduledTaskDefinition.getScheduledTask().getTask().getRunnable());
        thread.setName(scheduledTaskDefinition.getMethodName() + "_test_" + scheduledTaskDefinition.getId());
        thread.start();
    }

    @Override
    public void afterPropertiesSet() {
        refreshScheduledTaskDefinitions();
    }

    public List<ScheduledTaskDefinition> getScheduledTaskHolders() {
        if (needRefresh()) {
            refreshScheduledTaskDefinitions();
        }
        return scheduledTaskDefinitions;
    }

    private boolean needRefresh() {
        return scheduledTaskHolders.stream().mapToLong((holder) -> holder.getScheduledTasks().size()).sum() != scheduledTaskDefinitions.size();
    }

    private void refreshScheduledTaskDefinitions() {
        for (ScheduledTaskHolder scheduledTaskHolder : scheduledTaskHolders) {
            Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();
            for (ScheduledTask scheduledTask : scheduledTasks) {
                if(scheduledTaskDefinitions.stream().noneMatch(def -> def.getScheduledTask().equals(scheduledTask))) {
                    ScheduledTaskDefinition scheduledTaskDefinition = createScheduledTaskDefinition(scheduledTask);
                    if (scheduledTaskDefinition != null) {
                        scheduledTaskDefinitions.add(scheduledTaskDefinition);
                    }
                }
            }
        }
    }

    private ScheduledTaskDefinition createScheduledTaskDefinition(ScheduledTask scheduledTask) {
        ScheduledTaskDefinition scheduledTaskDefinition = new ScheduledTaskDefinition();
        scheduledTaskDefinition.setId(index.incrementAndGet());
        scheduledTaskDefinition.setScheduledTask(scheduledTask);
        scheduledTaskDefinition.setState(ScheduledTaskDefinition.State.SCHEDULING);
        Task task = scheduledTask.getTask();
        Runnable runnable = task.getRunnable();
        String methodName;
        if (runnable instanceof ScheduledMethodRunnable) {
            Method method = ((ScheduledMethodRunnable) runnable).getMethod();
            methodName = ClassUtils.getQualifiedMethodName(method);
        }
        else {
            methodName = runnable.getClass().getName();
        }
        scheduledTaskDefinition.setMethodName(methodName);
        ScheduledTaskDefinition.Type type;
        long initialDelay = -1;
        long interval = -1;
        String cronExpression = null;
        if (task instanceof FixedRateTask) {
            type = ScheduledTaskDefinition.Type.FIXED_RATE;
            FixedRateTask fixedRateTask = (FixedRateTask) task;
            initialDelay = fixedRateTask.getInitialDelay();
            interval = fixedRateTask.getInterval();
        } else if (task instanceof FixedDelayTask) {
            type = ScheduledTaskDefinition.Type.FIXED_DELAY;
            FixedDelayTask fixedDelayTask = (FixedDelayTask) task;
            initialDelay = fixedDelayTask.getInitialDelay();
            interval = fixedDelayTask.getInterval();
        } else if (task instanceof CronTask) {
            type = ScheduledTaskDefinition.Type.CRON;
            CronTask cronTask = (CronTask) task;
            cronExpression = cronTask.getExpression();
        } else {
            return null;
        }
        scheduledTaskDefinition.setType(type);
        scheduledTaskDefinition.setInitialDelay(initialDelay);
        scheduledTaskDefinition.setInterval(interval);
        scheduledTaskDefinition.setCronExpression(cronExpression);
        return scheduledTaskDefinition;
    }

    enum Action {
        CANCEL, EXECUTE
    }

}
