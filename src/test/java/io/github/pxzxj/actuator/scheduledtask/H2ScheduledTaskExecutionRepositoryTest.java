package io.github.pxzxj.actuator.scheduledtask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

public class H2ScheduledTaskExecutionRepositoryTest extends AbstractJdbcScheduledTaskExecutionRepositoryTest {

    static EmbeddedDatabase dataSource;

    @BeforeClass
    public static void startContainer() throws Exception {
        dataSource = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("classpath:MySQL_scheduledtask_execution.sql")
                .addScripts("classpath:MySQL_data.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        ScheduledProperties scheduledProperties = new ScheduledProperties();
        scheduledProperties.setRepositoryType(RepositoryType.JDBC);
        jdbcScheduledTaskExecutionRepository = new JdbcScheduledTaskExecutionRepository(scheduledProperties, jdbcTemplate, transactionTemplate);
        jdbcScheduledTaskExecutionRepository.afterPropertiesSet();
    }

    @AfterClass
    public static void stopContainer() {
        dataSource.shutdown();
    }

}
