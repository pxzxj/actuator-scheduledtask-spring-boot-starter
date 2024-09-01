package io.github.pxzxj.actuator.scheduledtask;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("management.scheduledtask")
public class ScheduledProperties {

    private RepositoryType repositoryType = RepositoryType.MEMORY;

    private int memorySizeLimit = 10000;

    private String jdbcTableName = "scheduledtask_execution";

    public RepositoryType getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
    }

    public int getMemorySizeLimit() {
        return memorySizeLimit;
    }

    public void setMemorySizeLimit(int memorySizeLimit) {
        this.memorySizeLimit = memorySizeLimit;
    }

    public String getJdbcTableName() {
        return jdbcTableName;
    }

    public void setJdbcTableName(String jdbcTableName) {
        this.jdbcTableName = jdbcTableName;
    }
}
