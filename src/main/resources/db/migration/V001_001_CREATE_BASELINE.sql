create table event (
    id identity not null,
    type varchar not null,
    data varchar,
    created_at timestamp not null,

    constraint event_pk primary key (id)
);

create table command (
    id identity not null,
    type varchar not null,
    data varchar
    created_at timestamp not null,

    constraint command_pk primary key (id)
);