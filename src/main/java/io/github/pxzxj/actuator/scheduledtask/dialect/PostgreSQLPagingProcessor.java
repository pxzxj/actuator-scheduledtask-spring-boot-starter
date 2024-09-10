package io.github.pxzxj.actuator.scheduledtask.dialect;

import java.util.List;

public class PostgreSQLPagingProcessor implements PagingProcessor {

    @Override
    public String getPagingSql(String originalSql) {
        return originalSql + " LIMIT ? OFFSET ?";
    }

    @Override
    public void processParameters(int page, int size, List<Object> parameters) {
        parameters.add(size);
        parameters.add(page * size);
    }
}
