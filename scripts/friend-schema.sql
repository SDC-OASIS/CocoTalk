create table friend
(
    id       bigint auto_increment primary key,
    from_uid bigint not null,
    to_uid   bigint not null,
    constraint user_friend_from_uid
        foreign key (from_uid) references user (id)
            on delete cascade,
    constraint user_friend_to_uid
        foreign key (to_uid) references user (id)
            on delete cascade
) engine=InnoDB;
