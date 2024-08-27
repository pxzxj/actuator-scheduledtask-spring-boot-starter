package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Aspect
public class ScheduledAspect {

    private final ScheduledTaskExecutionRepository scheduledTaskExecutionRepository;

    public ScheduledAspect(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        this.scheduledTaskExecutionRepository = scheduledTaskExecutionRepository;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || @annotation(org.springframework.scheduling.annotation.Schedules)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
        String methodName = joinPoint.getSignature().getName();
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(declaringType);
        LoggerContext loggerContext = logger.getLoggerContext();
        ByteArrayOutputStreamAppender byteArrayOutputStreamAppender = new ByteArrayOutputStreamAppender();
        byteArrayOutputStreamAppender.setName(methodName + "-byteArray-" + LocalDateTime.now());
        byteArrayOutputStreamAppender.setContext(loggerContext);
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(loggerContext);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);
        byteArrayOutputStreamAppender.setEncoder(encoder);
        byteArrayOutputStreamAppender.addFilter(new MethodNameFilter(methodName));
        byteArrayOutputStreamAppender.start();
        logger.addAppender(byteArrayOutputStreamAppender);
        ScheduledTaskExecution scheduledTaskExecution = new ScheduledTaskExecution();
        scheduledTaskExecution.setMethodName(declaringType.getName() + "." + methodName);
        scheduledTaskExecution.setStartTime(LocalDateTime.now());
        scheduledTaskExecution.setState(ScheduledTaskExecution.State.EXECUTING);
        scheduledTaskExecutionRepository.start(scheduledTaskExecution, byteArrayOutputStreamAppender);
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            scheduledTaskExecution.setException(readStackTrace(throwable));
            throw throwable;
        } finally {
            scheduledTaskExecution.setState(ScheduledTaskExecution.State.FINISHED);
            scheduledTaskExecution.setLog(byteArrayOutputStreamAppender.getLoggingContent());
            scheduledTaskExecution.setEndTime(LocalDateTime.now());
            scheduledTaskExecutionRepository.finish(scheduledTaskExecution);
            logger.detachAppender(byteArrayOutputStreamAppender);
            byteArrayOutputStreamAppender.stop();
            layout.stop();
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