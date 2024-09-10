CREATE TABLE scheduledtask_execution (
     id NUMBER PRIMARY KEY,
     method_name VARCHAR2(400),
     start_time TIMESTAMP,
     end_time TIMESTAMP,
     state VARCHAR2(200),
     log CLOB,
     exception CLOB
);