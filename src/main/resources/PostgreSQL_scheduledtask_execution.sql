CREATE TABLE scheduledtask_execution (
     id SERIAL PRIMARY KEY,
     method_name VARCHAR(400),
     start_time TIMESTAMP,
     end_time TIMESTAMP,
     state VARCHAR(200),
     log TEXT,
     exception TEXT
);