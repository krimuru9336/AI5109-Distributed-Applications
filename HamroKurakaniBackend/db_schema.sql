CREATE TABLE users (
    id VARCHAR(255),
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE chatgroups (
    id VARCHAR(255),
    name VARCHAR (255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE usergroups (
    id VARCHAR(255),
    userid VARCHAR(255),
    chatgroupid VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (userid) REFERENCES users(id),
    FOREIGN KEY (chatgroupid) REFERENCES chatgroups(id)
);

CREATE TABLE messages (
    id VARCHAR(255),
    sender_id VARCHAR(255) NOT NULL,
    sender_username VARCHAR(255),
    receiver_user_id VARCHAR(255),
    receiver_group_id VARCHAR(255),
    content VARCHAR(255) NOT NULL,
    content_type ENUM('text', 'image', 'gif', 'video'),
    is_edited BOOLEAN DEFAULT FALSE,
    sent_at DATETIME DEFAULT NOW(),
    PRIMARY KEY(id),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_user_id) REFERENCES users(id),
    FOREIGN KEY (receiver_group_id) REFERENCES chatgroups(id)
);