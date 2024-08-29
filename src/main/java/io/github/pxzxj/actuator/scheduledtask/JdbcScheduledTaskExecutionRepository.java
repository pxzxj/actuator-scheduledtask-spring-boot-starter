package io.github.pxzxj.actuator.scheduledtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

    private final Logger logger = LoggerFactory.getLogger(JdbcScheduledTaskExecutionRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final TransactionTemplate transactionTemplate;

    private final String tableName;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private final ConcurrentHashMap<Long, ByteArrayOutputStream> executingTaskLogs = new ConcurrentHashMap<>();

    public JdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.tableName = scheduledProperties.getJdbcTableName();
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(this.tableName)
                .usingColumns("method_name", "start_time", "state")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public boolean start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStream byteArrayOutputStream) {
        try {
            transactionTemplate.execute(status -> {
                Number number = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(scheduledTaskExecution));
                scheduledTaskExecution.setId(number.longValue());
                executingTaskLogs.put(scheduledTaskExecution.getId(), byteArrayOutputStream);
                return null;
            });
            return true;
        } catch (Throwable throwable) {
            logger.error("", throwable);
            return false;
        }
    }

    @Override
    public void finish(ScheduledTaskExecution scheduledTaskExecution) {
        try {
            transactionTemplate.execute(status -> {
                jdbcTemplate.update("update " + tableName + " set end_time=?,state=?,log=?,exception=? where id=?",
                        scheduledTaskExecution.getEndTime(), scheduledTaskExecution.getState().name(), scheduledTaskExecution.getLog(), scheduledTaskExecution.getException(), scheduledTaskExecution.getId());
                return null;
            });
        } catch (Throwable throwable) {
            logger.error("", throwable);
        }
        executingTaskLogs.remove(scheduledTaskExecution.getId());
    }

    @Override
    public Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size) {
        String sql = "select id,method_name,start_time,end_time,state,exception from " + tableName;
        String countSql = "select count(1) from " + tableName;
        List<String> conditions = new ArrayList<>();
        if (StringUtils.hasText(methodName)) {
            conditions.add("method_name like '%" + methodName + "%'");
        }
        if (StringUtils.hasText(startTimeStart) && StringUtils.hasText(startTimeEnd)) {
            conditions.add("start_time between '" + startTimeStart + "' and '" + startTimeEnd + "'");
        } else if(StringUtils.hasText(startTimeStart)) {
            conditions.add("start_time>'" + startTimeStart + "'");
        } else if (StringUtils.hasText(startTimeEnd)) {
            conditions.add("start_time<'" + startTimeEnd);
        }
        if (StringUtils.hasText(endTimeStart) && StringUtils.hasText(endTimeEnd)) {
            conditions.add("end_time between '" + endTimeStart + "' and '" + endTimeEnd + "'");
        } else if (StringUtils.hasText(endTimeStart)) {
            conditions.add("end_time>'" + endTimeStart + "'");
        } else if (StringUtils.hasText(endTimeEnd)) {
            conditions.add("end_time<'" + endTimeEnd + "'");
        }
        if (!conditions.isEmpty()) {
            String whereCondition = " where " + StringUtils.collectionToDelimitedString(conditions, " and ");
            sql += whereCondition;
            countSql += whereCondition;
        }
        sql += " order by id desc limit " + (page * size) + "," + size;
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        List<ScheduledTaskExecution> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ScheduledTaskExecution.class));
        return new Page<>(count, page, size, list);
    }

    @Override
    public String log(Long id) {
        ByteArrayOutputStream byteArrayOutputStream = executingTaskLogs.get(id);
        if (byteArrayOutputStream != null) {
            return byteArrayOutputStream.toString();
        }
        return (String) jdbcTemplate.queryForList("select log from " + tableName + " where id=?", id).get(0).get("log");
    }

}
