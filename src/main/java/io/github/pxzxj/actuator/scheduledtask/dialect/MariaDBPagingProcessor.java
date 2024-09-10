package io.github.pxzxj.actuator.scheduledtask.dialect;

import java.util.List;

public class MariaDBPagingProcessor implements PagingProcessor {

    private final PagingProcessor delegate = new MySQLPagingProcessor();

    @Override
    public String getPagingSql(String originalSql) {
        return delegate.getPagingSql(originalSql);
    }

    @Override
    public void processParameters(int page, int size, List<Object> parameters) {
        delegate.processParameters(page, size, parameters);
    }
}
