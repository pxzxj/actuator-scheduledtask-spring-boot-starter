package io.github.pxzxj;

import java.lang.reflect.Method;

public class ScheduledTaskDefinition {

    private Method method;

    private String methodName;

    private String cron;

    private State state;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
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
}
