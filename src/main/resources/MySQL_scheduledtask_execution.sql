create table scheduledtask_execution(
    id integer auto_increment primary key ,
    method_name varchar(400),
    start_time datetime,
    end_time datetime,
    state varchar(200),
    log longtext,
    exception longtext
);