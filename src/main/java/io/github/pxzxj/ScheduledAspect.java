package io.github.pxzxj;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Aspect
public class ScheduledAspect {

    private final TaskExecutionRepository taskExecutionRepository;

    public ScheduledAspect(TaskExecutionRepository taskExecutionRepository) {
        this.taskExecutionRepository = taskExecutionRepository;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || @annotation(org.springframework.scheduling.annotation.Schedules)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(declaringType);
        LoggerContext loggerContext = logger.getLoggerContext();
        ByteArrayOutputStreamAppender byteArrayOutputStreamAppender = new ByteArrayOutputStreamAppender();
        byteArrayOutputStreamAppender.setName(joinPoint.getSignature().getName() + LocalDateTime.now());
        byteArrayOutputStreamAppender.setContext(loggerContext);
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(loggerContext);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);
        byteArrayOutputStreamAppender.setEncoder(encoder);
        byteArrayOutputStreamAppender.start();
        logger.addAppender(byteArrayOutputStreamAppender);
        try {

            Object proceed = joinPoint.proceed();
            logger.detachAppender(byteArrayOutputStreamAppender);
            byteArrayOutputStreamAppender.stop();
            return proceed;
        } catch (Exception ex) {

            throw ex;
        } finally {
            byteArrayOutputStreamAppender.stop();
            layout.stop();
        }
    }
}