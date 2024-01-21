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
    FOREIGN KEY (userid) REFERENCES users(id),
    FOREIGN KEY (chatgroupid) REFERENCES chatgroups(id)
);