package io.github.pxzxj.actuator.scheduledtask.dialect;

import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

public interface PagingProcessor {

    List<PagingProcessor> pagingProcessors = Arrays.asList(new MySQLPagingProcessor(), new H2PagingProcessor(), new MariaDBPagingProcessor(),
            new OraclePagingProcessor(), new PostgreSQLPagingProcessor());

    String getPagingSql(String originalSql);

    /**
     *
     * @param page page number, 0 starting from 0
     * @param size page size
     * @param parameters query parameters
     */
    void processParameters(int page, int size, List<Object> parameters);

    @Nullable
    static PagingProcessor getPagingProcessorByDatabaseDriver(DatabaseDriver databaseDriver) {
        return pagingProcessors.stream().filter(p -> p.getClass().getSimpleName().toLowerCase().startsWith(databaseDriver.name().toLowerCase()))
                                        .findFirst()
                                        .orElse(null);
    }
}
