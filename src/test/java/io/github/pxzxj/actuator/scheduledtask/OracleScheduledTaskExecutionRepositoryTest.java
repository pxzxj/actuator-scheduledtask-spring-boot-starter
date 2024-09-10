package io.github.pxzxj.actuator.scheduledtask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.oracle.OracleContainer;

public class OracleScheduledTaskExecutionRepositoryTest extends AbstractJdbcScheduledTaskExecutionRepositoryTest {

    private static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withDatabaseName("testDB")
            .withUsername("testUser")
            .withPassword("testPassword")
            .withInitScripts("Oracle_scheduledtask_execution.sql", "Oracle_data.sql");


    @BeforeClass
    public static void startContainer() throws Exception {
        oracle.start();
        AbstractJdbcScheduledTaskExecutionRepositoryTest.initialize(oracle);
    }

    @AfterClass
    public static void stopContainer() {
        oracle.stop();
    }
}
