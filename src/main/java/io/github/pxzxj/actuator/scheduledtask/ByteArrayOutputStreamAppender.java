package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

import java.io.ByteArrayOutputStream;

public class ByteArrayOutputStreamAppender extends OutputStreamAppender<ILoggingEvent> {

    private ByteArrayOutputStream byteArrayOutputStream;

    @Override
    public void start() {
        setOutputStream(byteArrayOutputStream);
        super.start();
    }

    void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }
}
