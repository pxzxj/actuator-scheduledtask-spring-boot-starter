-- 1. EXECUTING 状态
INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log, exception)
VALUES (1, 'aaa', TO_TIMESTAMP('2023-04-01 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Starting execution...', NULL);

-- 2. FINISHED 状态
INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log, exception)
VALUES (2, 'bbb', TO_TIMESTAMP('2023-04-01 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-01 11:05:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'Execution completed successfully.', NULL);

-- 3. EXECUTING 状态
INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log)
VALUES (3, 'taskMethod3', TO_TIMESTAMP('2023-04-01 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Processing data...');

-- 4. FINISHED 状态，包含异常
INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log, exception)
VALUES (4, 'taskMethod4', TO_TIMESTAMP('2023-04-01 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-01 13:02:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'Execution failed to complete.', 'Error in processing data.');

-- 5-13 重复上述模式，改变方法名、时间和日志信息
INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log)
VALUES (5, 'taskMethod5', TO_TIMESTAMP('2023-04-02 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Initializing...');

INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log)
VALUES (6, 'taskMethod6', TO_TIMESTAMP('2023-04-02 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-02 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'Task completed with warnings.');

INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log, exception)
VALUES (7, 'taskMethod7', TO_TIMESTAMP('2023-04-02 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Sending notifications...', NULL);

INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log, exception)
VALUES (8, 'taskMethod8', TO_TIMESTAMP('2023-04-02 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-02 12:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'All notifications sent.', NULL);

INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log)
VALUES (9, 'taskMethod9', TO_TIMESTAMP('2023-04-03 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Running background checks...');

INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log, exception)
VALUES (10, 'taskMethod10', TO_TIMESTAMP('2023-04-03 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-03 09:10:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'Checks completed.', NULL);

INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log, exception)
VALUES (11, 'taskMethod11', TO_TIMESTAMP('2023-04-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Updating records...', NULL);

INSERT INTO scheduledtask_execution (id, method_name, start_time, end_time, state, log, exception)
VALUES (12, 'taskMethod12', TO_TIMESTAMP('2023-04-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2023-04-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'FINISHED', 'Records updated successfully.', NULL);

INSERT INTO scheduledtask_execution (id, method_name, start_time, state, log, exception)
VALUES (13, 'taskMethod13', TO_TIMESTAMP('2023-04-03 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'EXECUTING', 'Performing final checks...', NULL);