CREATE TABLE USERS
(
    user_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             VARCHAR(36) NOT NULL UNIQUE,
    email            VARCHAR(255) NOT NULL UNIQUE,
    nickname         VARCHAR(20) NOT NULL UNIQUE,
    password         VARCHAR(60) NOT NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
) ENGINE=InnoDB;

CREATE TABLE POST_LABELS
(
    post_label_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          ENUM ('OPENED', 'CLOSED', 'HOT') NOT NULL UNIQUE,
    is_deleted    TINYINT(1) DEFAULT 0 NOT NULL
) ENGINE=InnoDB;

CREATE TABLE POST_TAGS
(
    tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE POSTS
(
    post_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             VARCHAR(36) NOT NULL UNIQUE,
    title            VARCHAR(255) NOT NULL,
    content          TEXT NOT NULL,
    user_id          BIGINT NULL,
    is_deleted       TINYINT(1) DEFAULT 0 NOT NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_posts_user_id FOREIGN KEY (user_id) REFERENCES USERS (user_id)
) ENGINE=InnoDB;

CREATE TABLE APPLICATIONS
(
    application_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             VARCHAR(36) NOT NULL UNIQUE,
    user_id          BIGINT NOT NULL,
    post_id          BIGINT NOT NULL,
    status           ENUM ('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
    message          TEXT NOT NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_applications_user_id FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    CONSTRAINT fk_applications_post_id FOREIGN KEY (post_id) REFERENCES POSTS (post_id)
) ENGINE=InnoDB;

CREATE TABLE NOTIFICATIONS
(
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid            VARCHAR(36) NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL,
    type            ENUM ('GENERAL', 'APPLICATION_RECEIVED', 'APPLICATION_STATUS_UPDATE', 'POST_STATUS_UPDATE') NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         VARCHAR(500) NOT NULL,
    is_read         TINYINT(1) DEFAULT 0 NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_notifications_user_id FOREIGN KEY (user_id) REFERENCES USERS (user_id)
) ENGINE=InnoDB;

CREATE TABLE POST_LABEL_MAP
(
    post_id       BIGINT NOT NULL,
    post_label_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, post_label_id),
    CONSTRAINT fk_post_label_map_post_id FOREIGN KEY (post_id) REFERENCES POSTS (post_id),
    CONSTRAINT fk_post_label_map_post_label_id FOREIGN KEY (post_label_id) REFERENCES POST_LABELS (post_label_id)
) ENGINE=InnoDB;

CREATE TABLE POST_TAG_MAP
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tag_map_post_id FOREIGN KEY (post_id) REFERENCES POSTS (post_id),
    CONSTRAINT fk_post_tag_map_tag_id FOREIGN KEY (tag_id) REFERENCES POST_TAGS (tag_id)
) ENGINE=InnoDB;
