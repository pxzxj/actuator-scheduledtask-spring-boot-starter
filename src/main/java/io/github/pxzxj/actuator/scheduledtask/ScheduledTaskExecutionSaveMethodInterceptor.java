package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class ScheduledTaskExecutionSaveMethodInterceptor implements MethodInterceptor {

    private final ScheduledTaskExecutionRepository scheduledTaskExecutionRepository;

    public ScheduledTaskExecutionSaveMethodInterceptor(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        this.scheduledTaskExecutionRepository = scheduledTaskExecutionRepository;
    }


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> declaringType = AopUtils.getTargetClass(methodInvocation.getThis());
        String methodName = methodInvocation.getMethod().getName();
        String classMethodName = declaringType.getName() + "." + methodName;
        String appenderName = classMethodName + ".byteArrayAppender";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(declaringType);
        Appender<ILoggingEvent> appender = logger.getAppender(appenderName);
        ByteArrayOutputStreamAppender byteArrayOutputStreamAppender;
        if (appender != null) {
            byteArrayOutputStreamAppender = (ByteArrayOutputStreamAppender) appender;
            byteArrayOutputStreamAppender.setByteArrayOutputStream(byteArrayOutputStream);
            byteArrayOutputStreamAppender.start();
        } else {
            byteArrayOutputStreamAppender = new ByteArrayOutputStreamAppender();
            LoggerContext loggerContext = logger.getLoggerContext();
            byteArrayOutputStreamAppender.setName(appenderName);
            byteArrayOutputStreamAppender.setContext(loggerContext);
            byteArrayOutputStreamAppender.setByteArrayOutputStream(byteArrayOutputStream);
            LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
            encoder.setContext(loggerContext);
            TTLLLayout layout = new TTLLLayout();
            layout.setContext(loggerContext);
            layout.start();
            encoder.setLayout(layout);
            byteArrayOutputStreamAppender.setEncoder(encoder);
            byteArrayOutputStreamAppender.addFilter(new MethodNameFilter(declaringType.getName(), methodName));
            byteArrayOutputStreamAppender.start();
            logger.addAppender(byteArrayOutputStreamAppender);
        }
        ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
        scheduledTaskExecution.setMethodName(classMethodName);
        scheduledTaskExecution.setStartTime(LocalDateTime.now());
        scheduledTaskExecution.setState(ScheduledTaskExecution.State.EXECUTING);
        boolean start = scheduledTaskExecutionRepository.start(scheduledTaskExecution, byteArrayOutputStream);
        try {
            return methodInvocation.proceed();
        } catch (Throwable throwable) {
            scheduledTaskExecution.setException(readStackTrace(throwable));
            throw throwable;
        } finally {
            byteArrayOutputStreamAppender.stop();
            byteArrayOutputStreamAppender.setByteArrayOutputStream(null);
            if (start) {
                scheduledTaskExecution.setState(ScheduledTaskExecution.State.FINISHED);
                scheduledTaskExecution.setLog(byteArrayOutputStream.toString());
                scheduledTaskExecution.setEndTime(LocalDateTime.now());
                scheduledTaskExecutionRepository.finish(scheduledTaskExecution);
            }
        }
    }


    private String readStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }


}
