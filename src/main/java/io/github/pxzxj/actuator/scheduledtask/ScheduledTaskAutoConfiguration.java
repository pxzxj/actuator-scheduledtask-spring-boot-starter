package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.config.ScheduledTaskHolder;

import java.util.Collection;

//2.7.0之后使用@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class})
@Configuration
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
@ConditionalOnClass(Logger.class)
@EnableConfigurationProperties(ScheduledProperties.class)
public class ScheduledTaskAutoConfiguration {

    @Bean
    public ScheduledTaskDefinitionsEndpoint scheduledTaskDefinitionsEndpoint(Collection<ScheduledTaskHolder> scheduledTaskHolders) {
        return new ScheduledTaskDefinitionsEndpoint(scheduledTaskHolders);
    }

    @Bean
    public ScheduledTaskExecutionsEndpoint scheduledTaskExecutionsEndpoint(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        return new ScheduledTaskExecutionsEndpoint(scheduledTaskExecutionRepository);
    }

    @Bean
    public ScheduledTaskAspect scheduledTaskAspect(ScheduledTaskExecutionRepository scheduledTaskExecutionRepository) {
        return new ScheduledTaskAspect(scheduledTaskExecutionRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "memory", matchIfMissing = true)
    public ScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties) {
        return new InMemoryScheduledTaskExecutionRepository(scheduledProperties);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(JdbcTemplate.class)
    static class JdbcConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "management.schedule", name = "repository-type", havingValue = "jdbc")
        public ScheduledTaskExecutionRepository jdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate) {
            return new JdbcScheduledTaskExecutionRepository(scheduledProperties, jdbcTemplate);
        }
    }

}
