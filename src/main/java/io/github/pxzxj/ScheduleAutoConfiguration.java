package io.github.pxzxj;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class})
@ConditionalOnClass(Logger.class)
@EnableConfigurationProperties(ScheduleProperties.class)
public class ScheduleAutoConfiguration {

    @Bean
    public ScheduleEndpoint scheduleEndpoint(TaskExecutionRepository taskExecutionRepository) {
        return new ScheduleEndpoint(taskExecutionRepository);
    }

    @Bean
    public ScheduledAspect scheduledAspect(TaskExecutionRepository taskExecutionRepository) {
        return new ScheduledAspect(taskExecutionRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "memory", matchIfMissing = true)
    public TaskExecutionRepository inMemoryTaskExecutionRepository(ScheduleProperties scheduleProperties) {
        return new InMemoryTaskExecutionRepository(scheduleProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "jdbc")
    public TaskExecutionRepository jdbcTaskExecutionRepository(ScheduleProperties scheduleProperties, JdbcTemplate jdbcTemplate) {
        return new JdbcTaskExecutionRepository(scheduleProperties, jdbcTemplate);
    }

}
