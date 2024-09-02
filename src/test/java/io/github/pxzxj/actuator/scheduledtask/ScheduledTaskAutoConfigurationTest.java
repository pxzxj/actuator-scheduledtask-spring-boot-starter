package io.github.pxzxj.actuator.scheduledtask;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ScheduledTaskAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ScheduledTaskAutoConfiguration.class));

    @Test
    public void testBeans() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ScheduledTaskExecutionRepository.class);
                    assertThat(context).hasSingleBean(ScheduledTaskExecutionsEndpoint.class);
                    assertThat(context).hasSingleBean(ScheduledTaskDefinitionsEndpoint.class);

                });
    }

    @Test
    public void testRepositoryType() {
        contextRunner.withPropertyValues("management.scheduledtask.repository-type=jdbc")
                .withBean(JdbcTemplate.class, () -> new JdbcTemplate(new SimpleDriverDataSource()))
                .withBean(TransactionTemplate.class, () -> new TransactionTemplate(new DataSourceTransactionManager()))
                .run(context -> {
            assertThat(context).hasSingleBean(ScheduledTaskExecutionRepository.class);
            assertThat(context).getBean(ScheduledTaskExecutionRepository.class).isInstanceOf(JdbcScheduledTaskExecutionRepository.class);

        });
    }

    @Test
    public void testProperties() {
        contextRunner.withPropertyValues("management.scheduledtask.memory-size-limit=50")
                .run(context -> {
                    assertThat(context).getBean(ScheduledProperties.class).extracting("memorySizeLimit").isEqualTo(50);
                });
    }

    @Test
    public void testCustomizedRepositoryType() {
        contextRunner.withUserConfiguration(MyConfiguration.class).run(context -> {
            assertThat(context).getBean(ScheduledTaskExecutionRepository.class).isInstanceOf(MyScheduledTaskExecutionRepository.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class MyConfiguration {

        @Bean
        ScheduledTaskExecutionRepository myScheduledTaskExecution() {
            return new MyScheduledTaskExecutionRepository();
        }

    }

    static class MyScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

        @Override
        public boolean start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream) {
            return false;
        }

        @Override
        public void finish(ScheduledTaskExecution scheduledTaskExecution) {

        }

        @Override
        public Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size) {
            return null;
        }

        @Override
        public String log(Long id) {
            return null;
        }
    }
}