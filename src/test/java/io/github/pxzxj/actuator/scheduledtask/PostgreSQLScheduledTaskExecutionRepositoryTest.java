package io.github.pxzxj.actuator.scheduledtask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * 使用H2数据库模拟MySQL
 */
public class PostgreSQLScheduledTaskExecutionRepositoryTest extends AbstractJdbcScheduledTaskExecutionRepositoryTest {

    private static final PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withInitScripts("PostgreSQL_scheduledtask_execution.sql", "PostgreSQL_data.sql");

    @BeforeClass
    public static void startContainer() throws Exception {
        postgresql.start();
        AbstractJdbcScheduledTaskExecutionRepositoryTest.initialize(postgresql);
    }

    @AfterClass
    public static void stopContainer() {
        postgresql.stop();
    }

}