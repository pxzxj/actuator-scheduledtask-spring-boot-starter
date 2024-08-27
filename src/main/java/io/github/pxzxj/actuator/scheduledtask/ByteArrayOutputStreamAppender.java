package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

import java.io.ByteArrayOutputStream;

public class ByteArrayOutputStreamAppender extends OutputStreamAppender<ILoggingEvent> {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Override
    public void start() {
        setOutputStream(byteArrayOutputStream);
        super.start();
    }

    public String getLoggingContent() {
        return byteArrayOutputStream.toString();
    }

}
