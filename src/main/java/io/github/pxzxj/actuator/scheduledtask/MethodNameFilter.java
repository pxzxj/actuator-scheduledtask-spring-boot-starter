package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MethodNameFilter extends Filter<ILoggingEvent> {

    private final String expectedMethodName;

    public MethodNameFilter(String expectedMethodName) {
        this.expectedMethodName = expectedMethodName;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0 && !expectedMethodName.equals(cda[0].getMethodName())) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }

}
