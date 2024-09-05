package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MethodNameFilter extends Filter<ILoggingEvent> {

    private final String expectedClassName;
    private final String expectedMethodName;

    public MethodNameFilter(String expectedClassName, String expectedMethodName) {
        this.expectedClassName = expectedClassName;
        this.expectedMethodName = expectedMethodName;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0) {
            for (StackTraceElement stackTraceElement : cda) {
                if (stackTraceElement.getClassName().equals(expectedClassName) && stackTraceElement.getMethodName().equals(expectedMethodName)) {
                    return FilterReply.NEUTRAL;
                }
            }
            return FilterReply.DENY;

        } else {
            return FilterReply.NEUTRAL;
        }
    }

}
