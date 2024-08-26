package io.github.pxzxj;

public class ScheduleProperties {

    private RepositoryType repositoryType = RepositoryType.MEMORY;

    private int memorySizeLimit = 100000;

    private String jdbcTableName = "schedule_task_execution";

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
