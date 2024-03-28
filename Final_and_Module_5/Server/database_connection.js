const mysql = require('mysql');
const dotenv = require('dotenv');

dotenv.config();

// con is a mysql pool
let con = null;

const usersDB = process.env.USERS_DATABASE;
const messageDB = process.env.MESSAGE_DATABASE;
const usersTable = process.env.USERS_TABLE;
const groupTable = process.env.GROUP_TABLE;

console.log(usersDB);

async function initDatabase() {
    try {
        await initDatabaseConnection();
        await createChatDatabase();
        await createUserTable();
        await createMessageDatabase();
        await createGroupTable();
        console.log("Database initialization completed successfully.");
    } catch (error) {
        console.error("Database initialization failed:", error);
    }
}

async function initDatabaseConnection() {
    return new Promise((resolve, reject) => {
        try {
            con = mysql.createPool({
                connectionLimit: 10,
                host: process.env.MYSQL_HOST,
                user: process.env.MYSQL_USER,
                password: process.env.MYSQL_PASSWORD
            });

            con.getConnection(function(err, connection) {
                if (err) {
                    console.log("Error connecting to database");
                    reject(err);
                    return;
                }
                console.log("Connected to database");
                connection.release();
                resolve();
            });   
        } catch(error) {
            console.log("Error connecting to database");
            reject(error);
        }
    });
};

async function createChatDatabase() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE DATABASE IF NOT EXISTS ${usersDB}`, function(err, result) {
            if (err) {
                console.log("Error creating database");
                reject(err);
                return;
            }
            console.log("Database created");
            resolve();
        });
    });
}

async function createUserTable() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE TABLE IF NOT EXISTS ${usersDB}.${usersTable} (uuid VARCHAR(64) PRIMARY KEY, userName VARCHAR(255))`, function(err, result) {
            if (err) {
                console.log("Error creating table");
                reject(err);
                return;
            }
            console.log("Table created");
            resolve();
        });
    });
}

async function getAllUsers() {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM ${usersDB}.${usersTable}`, function(err, result) {
            if (err) {
                console.log("Error getting users");
                reject(err);
                return;
            }           
            resolve(JSON.parse(JSON.stringify(result)));
        });
    });
}

async function addUser(userName, uuid) {
    return new Promise((resolve, reject) => {
        con.query(`INSERT INTO ${usersDB}.${usersTable} (uuid, userName) VALUES (?, ?)`, [uuid, userName], function(err, result) {
            if (err) {
                console.log("Error adding user");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

async function createMessageDatabase() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE DATABASE IF NOT EXISTS ${messageDB}`, function(err, result) {
            if (err) {
                console.log("Error creating message database");
                reject(err);
                return;
            }

            console.log("Message database created");
            resolve();
        });
    });
}

async function createGroupTable() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE TABLE IF NOT EXISTS ${messageDB}.${groupTable} (
            id VARCHAR(64),
            groupName VARCHAR(255),
            isAdmin BOOLEAN,
            PRIMARY KEY (id, groupName)
        )`, function(err, result) {
            if (err) {
                console.log("Error creating group table");
                reject(err);
                return;
            }
            console.log("Group table created");
            resolve();
        });
    });
}

async function addUserToGroup(userID, groupName, isAdmin) {
    return new Promise((resolve, reject) => {
        con.query(`INSERT INTO ${messageDB}.${groupTable} (id, groupName, isAdmin) VALUES (?, ?, ?)`, [userID, groupName, isAdmin], function(err, result) {
            if (err) {
                console.log("Error adding user to group");
                reject(err);
                return;
            }
            console.log("User added to group");
            resolve(result);
        });
    });
}

async function removeUserFromGroup(userID, groupName) {
    return new Promise((resolve, reject) => {
        con.query(`DELETE FROM ${messageDB}.${groupTable} WHERE id = ? AND groupName = ?`, [userID, groupName], function(err, result) {
            if (err) {
                console.log("Error removing user from group");
                reject(err);
                return;
            }
            console.log("User removed from group");
            resolve(result);
        });
    });
}

async function getAllGroupEntries() {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM ${messageDB}.${groupTable}`, function(err, rows) {
            if (err) {
                console.log("Error getting groups");
                reject(err);
                return;
            }
            const groupEntries = rows.map(row => ({
                id: row.id,
                groupName: row.groupName,
                isAdmin: row.isAdmin
            }));
            console.log(groupEntries);
            console.log("Group Entries retrieved");
            resolve(groupEntries);
        });
    });
}

async function createMessageTableIfNotExist(tableName) {
    return new Promise((resolve, reject) => {
        con.query(`CREATE TABLE IF NOT EXISTS ${messageDB}.${tableName} (id VARCHAR(64) PRIMARY KEY, 
            partnerName VARCHAR(255), incoming BOOLEAN, messageText TEXT, timestamp BIGINT, 
            timestampEdit BIGINT DEFAULT NULL, deleted BOOLEAN DEFAULT false, chatGroup VARCHAR(255) DEFAULT NULL)`, function(err, result) {
            if (err) {
                console.log("Error creating message table");
                reject(err);
                return;
            }
            console.log("Message table created");
            resolve();
        });
    });
}

async function addMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`INSERT INTO ${messageDB}.${tableName} (id, partnerName, incoming, messageText, timestamp) VALUES (?, ?, ?, ?, ?)`, 
            [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp], function(err, result) {
            if (err) {
                console.log("Error adding message");
                reject(err);
                return;
            }
            console.log("Message added");
            resolve(result);
        });
    });
}

async function addEditMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM ${messageDB}.${tableName} WHERE id = ?`, [message.id], function(err, rows) {
            if (err) {
                console.log("Error checking if message exists");
                reject(err);
                return;
            }

            if (rows && rows.length > 0) {
                con.query(`UPDATE ${messageDB}.${tableName} SET messageText = ?, timestampEdit = ? WHERE id = ?`, 
                    [message.messageText, message.timestampEdit, message.id], function(err, result) {
                    if (err) {
                        console.log("Error updating message");
                        reject(err);
                        return;
                    }
                    console.log("Message updated");
                    resolve(result);
                });
            } else {
                con.query(`INSERT INTO ${messageDB}.${tableName} (id, partnerName, incoming, messageText, timestamp, timestampEdit) VALUES (?, ?, ?, ?, ?, ?)`, 
                    [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp, message.timestampEdit], function(err, result) {
                    if (err) {
                        console.log("Error adding message");
                        reject(err);
                        return;
                    }
                    console.log("Message added");
                    resolve(result);
                });
            }
        });
    });
}

async function addDeleteMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM ${messageDB}.${tableName} WHERE id = ?`, [message.id], function(err, rows) {
            if (err) {
                console.log("Error checking if message exists");
                reject(err);
                return;
            }

            if (rows && rows.length > 0) {
                con.query(`UPDATE ${messageDB}.${tableName} SET deleted = 1, messageText = ? WHERE id = ?`, [message.messageText, message.id], 
                function(err, result) {
                    if (err) {
                        console.log("Error deleting message");
                        reject(err);
                        return;
                    }
                    console.log("Message deleted");
                    resolve(result);
                });
            } else {
                con.query(`INSERT INTO ${messageDB}.${tableName} (id, partnerName, incoming, messageText, timestamp, deleted) VALUES (?, ?, ?, ?, ?, ?)`, 
                    [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp, 1], function(err, result) {
                    if (err) {
                        console.log("Error adding message");
                        reject(err);
                        return;
                    }
                    console.log("Message added");
                    resolve(result);
                });
            }
        });
    });
}

async function addGroupToMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM ${messageDB}.${tableName} WHERE id = ?`, [message.id], function(err, rows) {
            if (err) {
                console.log("Error checking if message exists");
                reject(err);
                return;
            }

            if (rows && rows.length > 0) {
                con.query(`UPDATE ${messageDB}.${tableName} SET chatGroup = ? WHERE id = ?`, 
                    [message.chatGroup, message.id], function(err, result) {
                    if (err) {
                        console.log("Error updating message");
                        reject(err);
                        return;
                    }
                    console.log("Message updated");
                    resolve(result);
                });
            }
        });
    });
}

async function getMessages(tableName) {
    return new Promise((resolve, reject) => {
        try {
            con.query(`SELECT * FROM ${messageDB}.${tableName} ORDER BY timestamp`, function(err, rows) {
                if (err) {
                    console.log("Error getting messages");
                    reject(err);
                    return;
                }

                const messages = rows.map(row => ({
                    id: row.id,
                    partnerName: row.partnerName,
                    incoming: row.incoming,
                    messageText: row.messageText,
                    timestamp: row.timestamp,
                    timestampEdit: row.timestampEdit,
                    deleted: row.deleted,
                    ...(row.chatGroup != null && { chatGroup: row.chatGroup })
                }));
                console.log(messages);

                console.log("Messages retrieved");
                resolve(messages);
            });
        } catch (error) {
            console.log("Error getting messages");
            reject();
        }
    });
}

async function clearTable(tableName) {
    return new Promise((resolve, reject) => {
        con.query(`DELETE FROM ${messageDB}.${tableName}`, function(err, result) {
            if (err) {
                console.log("Error clearing table");
                reject(err);
                return;
            }
            console.log("Table cleared");
            resolve(result);
        });
    });
}

module.exports = {
    init: initDatabase,
    getAllUsers,
    addUser,
    createUserMessageTable: createMessageTableIfNotExist,
    addMessage,
    addEditMessage,
    addDeleteMessage,
    getMessages,
    clearTable,
    getAllGroupEntries,
    addUserToGroup,
    removeUserFromGroup,
    addGroupToMessage
};