package io.github.pxzxj;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.config.ScheduledTaskHolder;

import java.util.Collection;

@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class})
@ConditionalOnClass(Logger.class)
@EnableConfigurationProperties(ScheduledProperties.class)
public class ScheduledAutoConfiguration {

    @Bean
    public ScheduledTaskDefinitionsEndpoint scheduledTaskDefinitionsEndpoint(Collection<ScheduledTaskHolder> scheduledTaskHolders) {
        return new ScheduledTaskDefinitionsEndpoint(scheduledTaskHolders);
    }

    @Bean
    public ScheduledTaskExecutionsEndpoint scheduledTaskExecutionsEndpoint(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        return new ScheduledTaskExecutionsEndpoint(scheduledTaskExecutionRepository);
    }

    @Bean
    public ScheduledAspect scheduledAspect(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        return new ScheduledAspect(scheduledTaskExecutionRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "memory", matchIfMissing = true)
    public InMemoryScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties) {
        return new InMemoryScheduledTaskExecutionRepository(scheduledProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "jdbc")
    public JdbcScheduledTaskExecutionRepository jdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate) {
        return new JdbcScheduledTaskExecutionRepository(scheduledProperties, jdbcTemplate);
    }

}
