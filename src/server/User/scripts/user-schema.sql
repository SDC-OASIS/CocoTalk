create table user
(
    id          bigint auto_increment primary key,
    cid         varchar(20)  not null,
    password    varchar(64)  not null,
    user_name   varchar(20)  not null,
    nickname    varchar(20)  not null,
    email       varchar(125),
    phone       varchar(20)  not null,
    profile     varchar(255),
    provider    varchar(255) not null,
    provider_id varchar(255) not null,
    status      smallint     not null,
    birth       timestamp,
    loggedin_at  timestamp,
    created_at  timestamp    not null,
    modified_at timestamp    not null,
    constraint unique_phone
        unique (phone),
    constraint unique_email
        unique (email)
) engine=InnoDB;