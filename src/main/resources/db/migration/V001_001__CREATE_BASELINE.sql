create table event (
    id identity not null,
    item_id varchar not null,
    type varchar not null,
    data varchar,
    created_at timestamp not null,
    created_by varchar not null,

    constraint event_pk primary key (id)
);

create table command (
    id identity not null,
    item_id varchar not null,
    type varchar not null,
    data varchar,
    completed boolean not null,
    created_at timestamp not null,
    created_by varchar not null,

    constraint command_pk primary key (id)
);

CREATE TABLE shedlock (
    name varchar(64) NOT NULL,
    lock_until timestamp NOT NULL,
    locked_at timestamp NOT NULL,
    locked_by varchar(255) NOT NULL,
    constraint shedlock_pk PRIMARY KEY (name)
);