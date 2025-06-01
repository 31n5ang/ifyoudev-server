create table POST_LABELS
(
    post_label_id bigint auto_increment comment '공고 라벨 고유 ID'
        primary key,
    name          enum ('OPENED', 'CLOSED', 'HOT') not null comment '라벨명 (예: 모집 중, 마감, 인기)',
    is_deleted    tinyint(1) default 0             not null comment '라벨의 삭제 여부',
    constraint name
        unique (name)
);

create table POST_TAGS
(
    tag_id bigint auto_increment comment '태그 고유 ID'
        primary key,
    name   varchar(40) not null comment '태그명 (예: JAVA, Python)',
    constraint name
        unique (name)
);

create table USERS
(
    user_id          bigint auto_increment comment '사용자 고유 ID'
        primary key,
    uuid             varchar(36)                        not null comment '외부 시스템에서 사용될 사용자 식별자',
    email            varchar(255)                       not null comment '사용자 이메일 주소',
    nickname         varchar(20)                        not null comment '사용자 닉네임',
    password         varchar(60)                        not null comment 'bcrypt로 해시된 사용자 비밀번호',
    created_at       datetime default CURRENT_TIMESTAMP not null comment '사용자 생성일시',
    last_modified_at datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '마지막 정보 수정일시',
    constraint email
        unique (email),
    constraint nickname
        unique (nickname),
    constraint uuid
        unique (uuid)
);

create table NOTIFICATIONS
(
    notification_id bigint auto_increment comment '알림 고유 ID'
        primary key,
    uuid            varchar(36)                                                                                 not null comment '외부 시스템에서 사용될 알림 식별자',
    user_id         bigint                                                                                      not null comment '알림을 받을 사용자 ID (외래 키)',
    type            enum ('GENERAL', 'APPLICATION_RECEIVED', 'APPLICATION_STATUS_UPDATE', 'POST_STATUS_UPDATE') not null comment '알림 유형',
    title           varchar(255)                                                                                not null comment '알림 제목',
    message         varchar(500)                                                                                not null comment '알림 내용',
    is_read         tinyint(1) default 0                                                                        not null comment '알림 읽음 여부',
    created_at      datetime   default CURRENT_TIMESTAMP                                                        not null comment '알림 생성일시',
    constraint uuid
        unique (uuid),
    constraint NOTIFICATIONS_ibfk_1
        foreign key (user_id) references USERS (user_id)
);

create index user_id
    on NOTIFICATIONS (user_id);

create table POSTS
(
    post_id          bigint auto_increment comment '모집 공고 고유 ID'
        primary key,
    uuid             varchar(36)                          not null comment '외부 시스템에서 사용될 공고 식별자',
    title            varchar(255)                         not null comment '모집 공고 제목',
    content          text                                 not null comment '모집 공고 본문 내용',
    user_id          bigint                               null comment '공고를 작성한 사용자 ID (외래 키)',
    is_deleted       tinyint(1) default 0                 not null comment '공고의 삭제 여부',
    created_at       datetime   default CURRENT_TIMESTAMP not null comment '공고 생성일시',
    last_modified_at datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '마지막 공고 수정일시',
    constraint uuid
        unique (uuid),
    constraint POSTS_ibfk_1
        foreign key (user_id) references USERS (user_id)
);

create table APPLICATIONS
(
    application_id   bigint auto_increment comment '신청서 고유 ID'
        primary key,
    uuid             varchar(36)                              not null comment '외부 시스템에서 사용될 신청서 식별자',
    user_id          bigint                                   not null comment '신청서를 작성한 사용자 ID (외래 키)',
    post_id          bigint                                   not null comment '신청서가 연결된 모집 공고 ID (외래 키)',
    status           enum ('PENDING', 'APPROVED', 'REJECTED') not null comment '지원 상태 (대기중, 승인됨, 거절됨)',
    message          text                                     not null comment '자기소개 또는 메시지 내용',
    created_at       datetime default CURRENT_TIMESTAMP       not null comment '신청서 생성일시',
    last_modified_at datetime default CURRENT_TIMESTAMP       not null on update CURRENT_TIMESTAMP comment '마지막 신청서 수정일시',
    constraint uuid
        unique (uuid),
    constraint APPLICATIONS_ibfk_1
        foreign key (user_id) references USERS (user_id),
    constraint APPLICATIONS_ibfk_2
        foreign key (post_id) references POSTS (post_id)
);

create index post_id
    on APPLICATIONS (post_id);

create index user_id
    on APPLICATIONS (user_id);

create index user_id
    on POSTS (user_id);

create table POST_LABEL_MAP
(
    post_id       bigint not null comment '모집 공고 ID (외래 키)',
    post_label_id bigint not null comment '공고 라벨 ID (외래 키)',
    primary key (post_id, post_label_id),
    constraint POST_LABEL_MAP_ibfk_1
        foreign key (post_id) references POSTS (post_id),
    constraint POST_LABEL_MAP_ibfk_2
        foreign key (post_label_id) references POST_LABELS (post_label_id)
);

create index post_label_id
    on POST_LABEL_MAP (post_label_id);

create table POST_TAG_MAP
(
    post_id bigint not null comment '모집 공고 ID (외래 키)',
    tag_id  bigint not null comment '태그 ID (외래 키)',
    primary key (post_id, tag_id),
    constraint POST_TAG_MAP_ibfk_1
        foreign key (post_id) references POSTS (post_id),
    constraint POST_TAG_MAP_ibfk_2
        foreign key (tag_id) references POST_TAGS (tag_id)
);

create index tag_id
    on POST_TAG_MAP (tag_id);

