package io.github.pxzxj.actuator.scheduledtask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * 使用H2数据库模拟MySQL
 */
public class MySQLScheduledTaskExecutionRepositoryTest extends AbstractJdbcScheduledTaskExecutionRepositoryTest {

    private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql").withTag("5.7.34"))
            .withUrlParam("serverTimezone", "Asia/Shanghai")
            .withInitScripts("MySQL_scheduledtask_execution.sql", "MySQL_data.sql");

    @BeforeClass
    public static void startContainer() throws Exception {
        mysql.start();
        AbstractJdbcScheduledTaskExecutionRepositoryTest.initialize(mysql);
    }

    @AfterClass
    public static void stopContainer() {
        mysql.stop();
    }

}