-- 1. EXECUTING 状态
INSERT INTO scheduledtask_execution (method_name, start_time, state, log, exception)
VALUES ('aaa', '2023-04-01 10:00:00', 'EXECUTING', 'Starting execution...', NULL);

-- 2. FINISHED 状态
INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log, exception)
VALUES ('bbb', '2023-04-01 11:00:00', '2023-04-01 11:05:00', 'FINISHED', 'Execution completed successfully.', NULL);

-- 3. EXECUTING 状态
INSERT INTO scheduledtask_execution (method_name, start_time, state, log)
VALUES ('taskMethod3', '2023-04-01 12:00:00', 'EXECUTING', 'Processing data...');

-- 4. FINISHED 状态，包含异常
INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log, exception)
VALUES ('taskMethod4', '2023-04-01 13:00:00', '2023-04-01 13:02:00', 'FINISHED', 'Execution failed to complete.', 'Error in processing data.');

-- 5-13 重复上述模式，改变方法名、时间和日志信息
INSERT INTO scheduledtask_execution (method_name, start_time, state, log)
VALUES ('taskMethod5', '2023-04-02 09:00:00', 'EXECUTING', 'Initializing...');

INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log)
VALUES ('taskMethod6', '2023-04-02 10:00:00', '2023-04-02 10:30:00', 'FINISHED', 'Task completed with warnings.');

INSERT INTO scheduledtask_execution (method_name, start_time, state, log, exception)
VALUES ('taskMethod7', '2023-04-02 11:00:00', 'EXECUTING', 'Sending notifications...', NULL);

INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log, exception)
VALUES ('taskMethod8', '2023-04-02 12:00:00', '2023-04-02 12:15:00', 'FINISHED', 'All notifications sent.', NULL);

INSERT INTO scheduledtask_execution (method_name, start_time, state, log)
VALUES ('taskMethod9', '2023-04-03 08:00:00', 'EXECUTING', 'Running background checks...');

INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log, exception)
VALUES ('taskMethod10', '2023-04-03 09:00:00', '2023-04-03 09:10:00', 'FINISHED', 'Checks completed.', NULL);

INSERT INTO scheduledtask_execution (method_name, start_time, state, log, exception)
VALUES ('taskMethod11', '2023-04-03 10:00:00', 'EXECUTING', 'Updating records...', NULL);

INSERT INTO scheduledtask_execution (method_name, start_time, end_time, state, log, exception)
VALUES ('taskMethod12', '2023-04-03 11:00:00', '2023-04-03 11:30:00', 'FINISHED', 'Records updated successfully.', NULL);

INSERT INTO scheduledtask_execution (method_name, start_time, state, log, exception)
VALUES ('taskMethod13', '2023-04-03 12:00:00', 'EXECUTING', 'Performing final checks...', NULL);