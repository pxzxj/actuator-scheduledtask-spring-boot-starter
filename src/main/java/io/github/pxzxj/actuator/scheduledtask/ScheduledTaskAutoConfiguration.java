package io.github.pxzxj.actuator.scheduledtask;

import ch.qos.logback.classic.Logger;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;

//2.7.0之后使用@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, TransactionAutoConfiguration.class})
@ConditionalOnClass(Logger.class)
@EnableConfigurationProperties(ScheduledProperties.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultPointcutAdvisor scheduledTaskExecutionSaveAdvisor(ScheduledTaskExecutionSaveMethodInterceptor scheduledTaskExecutionSaveMethodInterceptor) {
        return new DefaultPointcutAdvisor(AnnotationMatchingPointcut.forMethodAnnotation(Scheduled.class),
                scheduledTaskExecutionSaveMethodInterceptor);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ScheduledTaskExecutionSaveMethodInterceptor scheduledTaskExecutionSaveMethodInterceptor() {
        return new ScheduledTaskExecutionSaveMethodInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "management.scheduledtask", name = "repository-type", havingValue = "memory", matchIfMissing = true)
    public ScheduledTaskExecutionRepository inMemoryScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties) {
        return new InMemoryScheduledTaskExecutionRepository(scheduledProperties);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(JdbcTemplate.class)
    static class JdbcConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "management.scheduledtask", name = "repository-type", havingValue = "jdbc")
        public ScheduledTaskExecutionRepository jdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
            return new JdbcScheduledTaskExecutionRepository(scheduledProperties, jdbcTemplate, transactionTemplate);
        }
    }

    /**
     * backport commit <a href="https://github.com/spring-projects/spring-boot/commit/7aabd8bf2d401504636bfe6ce00e57ea8ac7d473">
     * Ensure that class proxying is forced before AutoProxyCreator is created</a>
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("org.aspectj.weaver.Advice")
    @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
            matchIfMissing = true)
    static class ClassProxyingConfigurationBackport {

        @Bean
        static BeanFactoryPostProcessor forceAutoProxyCreatorToUseClassProxyingBackport() {
            return (beanFactory) -> {
                if (beanFactory instanceof BeanDefinitionRegistry) {
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                    AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                }
            };
        }

    }

}
