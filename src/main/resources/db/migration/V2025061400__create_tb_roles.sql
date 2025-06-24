CREATE TABLE ROLES (
    role_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE USER_ROLE_MAP (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_map_user_id FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    CONSTRAINT fk_user_role_map_role_id FOREIGN KEY (role_id) REFERENCES ROLES (role_id)
) ENGINE=InnoDB;
