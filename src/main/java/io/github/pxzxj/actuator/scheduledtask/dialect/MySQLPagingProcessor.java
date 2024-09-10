package io.github.pxzxj.actuator.scheduledtask.dialect;

import java.util.List;

public class MySQLPagingProcessor implements PagingProcessor {

    @Override
    public String getPagingSql(String originalSql) {
        return originalSql + " limit ?,?";
    }

    @Override
    public void processParameters(int page, int size, List<Object> parameters) {
        parameters.add(page * size);
        parameters.add(size);
    }
}
