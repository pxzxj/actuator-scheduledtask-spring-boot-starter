package io.github.pxzxj.actuator.scheduledtask;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private final ConcurrentHashMap<Long, ByteArrayOutputStreamAppender> executingTaskLogs = new ConcurrentHashMap<>();

    public JdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(scheduledProperties.getJdbcTableName())
                .usingColumns("method_name", "start_time", "state")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void start(ScheduledTaskExecution scheduledTaskExecution, ByteArrayOutputStreamAppender byteArrayOutputStreamAppender) {
        Number number = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(scheduledTaskExecution));
        scheduledTaskExecution.setId(number.longValue());
        executingTaskLogs.put(scheduledTaskExecution.getId(), byteArrayOutputStreamAppender);
    }

    @Override
    public void finish(ScheduledTaskExecution scheduledTaskExecution) {
        jdbcTemplate.update("update " + simpleJdbcInsert.getTableName() + " set end_time=?,state=?,log=?,exception=? where id=?",
                scheduledTaskExecution.getEndTime(), scheduledTaskExecution.getState(), scheduledTaskExecution.getLog(), scheduledTaskExecution.getException(), scheduledTaskExecution.getId());
        executingTaskLogs.remove(scheduledTaskExecution.getId());
    }

    @Override
    public Page<ScheduledTaskExecution> page(String methodName, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd, int page, int size) {
        String sql = "select id,method_name,start_time,end_time,state,exception from " + simpleJdbcInsert.getTableName();
        String countSql = "select count(1) from " + simpleJdbcInsert.getTableName();
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
            String whereCondition = StringUtils.collectionToDelimitedString(conditions, " and ", "where ", "");
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
        ByteArrayOutputStreamAppender byteArrayOutputStreamAppender = executingTaskLogs.get(id);
        if (byteArrayOutputStreamAppender != null) {
            return byteArrayOutputStreamAppender.getLoggingContent();
        }
        return (String) jdbcTemplate.queryForList("select log from " + simpleJdbcInsert.getTableName() + " where id=?", id).get(0).get("log");
    }

}
