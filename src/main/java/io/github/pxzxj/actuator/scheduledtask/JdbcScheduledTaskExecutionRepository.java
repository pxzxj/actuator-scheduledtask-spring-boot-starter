package io.github.pxzxj.actuator.scheduledtask;

import io.github.pxzxj.actuator.scheduledtask.dialect.PagingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcScheduledTaskExecutionRepository implements ScheduledTaskExecutionRepository, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(JdbcScheduledTaskExecutionRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final TransactionTemplate transactionTemplate;

    private final String tableName;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private PagingProcessor pagingProcessor;

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

    public JdbcScheduledTaskExecutionRepository(ScheduledProperties scheduledProperties, JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, PagingProcessor pagingProcessor) {
        this(scheduledProperties, jdbcTemplate, transactionTemplate);
        this.pagingProcessor = pagingProcessor;
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
        List<Object> parameters = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        if (StringUtils.hasText(methodName)) {
            conditions.add("method_name like ?");
            parameters.add("%" + methodName + "%");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.hasText(startTimeStart) && StringUtils.hasText(startTimeEnd)) {
            conditions.add("start_time between ? and ?");
            parameters.add(LocalDateTime.parse(startTimeStart, dateTimeFormatter));
            parameters.add(LocalDateTime.parse(startTimeEnd, dateTimeFormatter));
        } else if(StringUtils.hasText(startTimeStart)) {
            conditions.add("start_time > ?");
            parameters.add(LocalDateTime.parse(startTimeStart, dateTimeFormatter));
        } else if (StringUtils.hasText(startTimeEnd)) {
            conditions.add("start_time < ?");
            parameters.add(LocalDateTime.parse(startTimeEnd, dateTimeFormatter));
        }
        if (StringUtils.hasText(endTimeStart) && StringUtils.hasText(endTimeEnd)) {
            conditions.add("end_time between ? and ?");
            parameters.add(LocalDateTime.parse(endTimeStart, dateTimeFormatter));
            parameters.add(LocalDateTime.parse(endTimeEnd, dateTimeFormatter));
        } else if (StringUtils.hasText(endTimeStart)) {
            conditions.add("end_time > ?");
            parameters.add(LocalDateTime.parse(endTimeStart, dateTimeFormatter));
        } else if (StringUtils.hasText(endTimeEnd)) {
            conditions.add("end_time < ?");
            parameters.add(LocalDateTime.parse(endTimeEnd, dateTimeFormatter));
        }
        if (!conditions.isEmpty()) {
            String whereCondition = " where " + StringUtils.collectionToDelimitedString(conditions, " and ");
            sql += whereCondition;
            countSql += whereCondition;
        }
        Integer count = jdbcTemplate.queryForObject(countSql, parameters.toArray(), Integer.class);
        sql += " order by id desc ";
        sql = pagingProcessor.getPagingSql(sql);
        pagingProcessor.processParameters(page, size, parameters);
        List<ScheduledTaskExecution> list = jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper<>(ScheduledTaskExecution.class));
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

    @Override
    public void afterPropertiesSet() throws Exception {
        if (pagingProcessor == null) {
            DatabaseDriver databaseDriver = getDatabaseDriver(jdbcTemplate.getDataSource());
            Assert.state(databaseDriver != DatabaseDriver.UNKNOWN, "Unable to detect database type");
            pagingProcessor = PagingProcessor.getPagingProcessorByDatabaseDriver(databaseDriver);
            Assert.notNull(pagingProcessor, "no PagingProcessor found for " + databaseDriver);
        }
    }

    DatabaseDriver getDatabaseDriver(DataSource dataSource) {
        try {
            String productName = JdbcUtils.commonDatabaseName(
                    JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getDatabaseProductName));
            return DatabaseDriver.fromProductName(productName);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to determine DatabaseDriver", ex);
        }
    }
}
