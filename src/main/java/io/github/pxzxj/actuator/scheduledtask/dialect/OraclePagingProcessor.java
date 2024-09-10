package io.github.pxzxj.actuator.scheduledtask.dialect;

import java.util.List;

public class OraclePagingProcessor implements PagingProcessor {

    @Override
    public String getPagingSql(String originalSql) {
        String sql = "SELECT * FROM ( " +
                " SELECT TMP_PAGE.*, ROWNUM PAGEHELPER_ROW_ID FROM ( " +
                originalSql +
                " ) TMP_PAGE)" +
                " WHERE PAGEHELPER_ROW_ID <= ? AND PAGEHELPER_ROW_ID > ?";
        return sql;
    }

    @Override
    public void processParameters(int page, int size, List<Object> parameters) {
        parameters.add(page * size + size);
        parameters.add(page * size);
    }
}
