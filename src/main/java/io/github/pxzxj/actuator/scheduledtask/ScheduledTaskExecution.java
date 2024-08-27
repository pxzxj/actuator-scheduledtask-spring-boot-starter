package io.github.pxzxj.actuator.scheduledtask;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class ScheduledTaskExecution {

    private Long id;

    private String methodName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private State state;

    @JsonIgnore
    private String log;

    private String exception;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    enum State {

        EXECUTING, FINISHED;

    }
}
