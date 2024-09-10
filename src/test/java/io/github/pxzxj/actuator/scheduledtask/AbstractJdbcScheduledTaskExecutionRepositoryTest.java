package io.github.pxzxj.actuator.scheduledtask;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

public abstract class AbstractJdbcScheduledTaskExecutionRepositoryTest {

    static JdbcScheduledTaskExecutionRepository jdbcScheduledTaskExecutionRepository;

    static void initialize(JdbcDatabaseContainer<?> jdbcDatabaseContainer) throws Exception {
        DataSource dataSource = new DriverManagerDataSource(jdbcDatabaseContainer.getJdbcUrl(), jdbcDatabaseContainer.getUsername(), jdbcDatabaseContainer.getPassword());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        ScheduledProperties scheduledProperties = new ScheduledProperties();
        scheduledProperties.setRepositoryType(RepositoryType.JDBC);
        jdbcScheduledTaskExecutionRepository = new JdbcScheduledTaskExecutionRepository(scheduledProperties, jdbcTemplate, transactionTemplate);
        jdbcScheduledTaskExecutionRepository.afterPropertiesSet();
    }

    @Test
    public void testPage() {
        System.out.println("jdbcScheduledTaskExecutionRepository = " + jdbcScheduledTaskExecutionRepository);
        System.out.println("clazz = " + this.getClass());
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page(null, null, null, null, null, 0, 10);
        Assert.assertEquals(13, page.getTotal());
        Assert.assertEquals(10, page.getData().size());
        Assert.assertEquals("taskMethod13", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilter() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod1", null, null, null, null, 0, 10);
        Assert.assertEquals(4, page.getTotal());
        Assert.assertEquals("taskMethod13", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeGreater() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", "2023-04-01 13:00:00", null, null, null, 0, 10);
        Assert.assertEquals(9, page.getTotal());
        Assert.assertEquals("taskMethod13", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeLess() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", null, "2023-04-03 10:00:00", null, null, 0, 10);
        Assert.assertEquals(8, page.getTotal());
        Assert.assertEquals("taskMethod10", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeBetween() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", "2023-04-01 13:00:00", "2023-04-03 10:00:00", null, null, 0, 10);
        Assert.assertEquals(8, page.getTotal());
        Assert.assertEquals("taskMethod11", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeBetweenAndEndTimeGreater() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", "2023-04-01 13:00:00", "2023-04-03 10:00:00", "2023-04-01 13:02:00", null, 0, 10);
        Assert.assertEquals(3, page.getTotal());
        Assert.assertEquals("taskMethod10", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeBetweenAndEndTimeLess() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", "2023-04-01 13:00:00", "2023-04-03 10:00:00", null, "2023-04-03 09:10:00", 0, 10);
        Assert.assertEquals(3, page.getTotal());
        Assert.assertEquals("taskMethod8", page.getData().get(0).getMethodName());
    }

    @Test
    public void testPageMethodFilterAndStartTimeBetweenAndEndTimeBetween() {
        Page<ScheduledTaskExecution> page = jdbcScheduledTaskExecutionRepository.page("taskMethod", "2023-04-01 13:00:00", "2023-04-03 10:00:00", "2023-04-01 13:02:00", "2023-04-03 09:10:00", 0, 10);
        Assert.assertEquals(4, page.getTotal());
        Assert.assertEquals("taskMethod10", page.getData().get(0).getMethodName());
    }
}
