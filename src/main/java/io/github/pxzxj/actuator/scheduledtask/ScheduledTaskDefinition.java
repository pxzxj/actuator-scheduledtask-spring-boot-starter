package io.github.pxzxj.actuator.scheduledtask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.scheduling.config.ScheduledTask;

public class ScheduledTaskDefinition {

    private Integer id;

    @JsonIgnore
    private ScheduledTask scheduledTask;

    private Type type;

    private String methodName;

    private long initialDelay;

    private long interval;

    private String cronExpression;

    private State state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }

    public void setScheduledTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    enum State {
        SCHEDULING, CANCELED;
    }

    enum Type {
        CRON, CUSTOM_TRIGGER, FIXED_DELAY, FIXED_RATE;
    }
}
