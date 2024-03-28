const mysql = require('mysql');
const dotenv = require('dotenv');

dotenv.config();

// con is a mysql pool
let con = null;

const usersDB = process.env.USERS_DATABASE;
const messageDB = process.env.MESSAGE_DATABASE;
const usersTable = process.env.USERS_TABLE;
const groupTable = process.env.GROUP_TABLE;

let isInitialized = false;

/**
 * Initializes the database by establishing a connection and creating necessary tables.
 * 
 * @returns {Promise<void>} A promise that resolves when the database initialization is completed successfully.
 */
async function initDatabase() {
    try {
        console.log("Trying to initialize database...");
        await initDatabaseConnection();
        await createChatDatabase();
        await createUserTable();
        await createMessageDatabase();
        await createGroupTable();
        console.log("Database initialization completed successfully.");
    } catch (error) {
        console.error("Database initialization failed. Proceeding without database connection. Offline messages will not be sent.");
    }
}

/**
 * Initializes the database connection.
 * 
 * @returns {Promise<void>} A promise that resolves when the database connection is successfully established.
 */
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
                isInitialized = true;
                resolve();
            });   
        } catch(error) {
            console.log("Error connecting to database");
            reject(error);
        }
    });
};

/**
 * Creates a chat database if it doesn't already exist.
 * 
 * @returns {Promise<void>} A promise that resolves when the database is created successfully.
 */
async function createChatDatabase() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE DATABASE IF NOT EXISTS ${usersDB}`, function(err, result) {
            if (err) {
                console.log("Error creating database");
                reject(err);
                return;
            }
            resolve();
        });
    });
}

/**
 * Creates a user table in the database if it doesn't already exist.
 * 
 * @returns {Promise<void>} A promise that resolves when the table is created successfully.
 */
async function createUserTable() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE TABLE IF NOT EXISTS ${usersDB}.${usersTable} (uuid VARCHAR(64) PRIMARY KEY, userName VARCHAR(255))`, function(err, result) {
            if (err) {
                console.log("Error creating table");
                reject(err);
                return;
            }
            resolve();
        });
    });
}

/**
 * Retrieves all users from the database.
 * 
 * @returns {Promise<Array<Object>>} A promise that resolves to an array of user objects.
 */
async function getAllUsers() {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve([]);
            return;
        }
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

/**
 * Adds a user to the database.
 * 
 * @param {string} userName - The name of the user.
 * @param {string} uuid - The UUID of the user.
 * @returns {Promise<any>} - A promise that resolves with the result of the database query.
 */
async function addUser(userName, uuid) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
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

/**
 * Creates a message database if it does not already exist.
 * 
 * @returns {Promise<void>} A promise that resolves when the database is created successfully.
 */
async function createMessageDatabase() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE DATABASE IF NOT EXISTS ${messageDB}`, function(err, result) {
            if (err) {
                console.log("Error creating message database");
                reject(err);
                return;
            }

            resolve();
        });
    });
}

/**
 * Creates a group table in the database if it does not already exist.
 * 
 * @returns {Promise<void>} A promise that resolves when the group table is created successfully.
 */
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
            resolve();
        });
    });
}

/**
 * Adds a user to a group.
 * 
 * @param {number} userID - The ID of the user.
 * @param {string} groupName - The name of the group.
 * @param {boolean} isAdmin - Indicates whether the user is an admin of the group.
 * @returns {Promise<any>} A promise that resolves with the result of the insertion or rejects with an error.
 */
async function addUserToGroup(userID, groupName, isAdmin) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
        con.query(`INSERT INTO ${messageDB}.${groupTable} (id, groupName, isAdmin) VALUES (?, ?, ?)`, [userID, groupName, isAdmin], function(err, result) {
            if (err) {
                console.log("Error adding user to group");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

/**
 * Removes a user from a group.
 *
 * @param {number} userID - The ID of the user to be removed.
 * @param {string} groupName - The name of the group from which the user will be removed.
 * @returns {Promise<any>} A promise that resolves with the result of the removal operation.
 */
async function removeUserFromGroup(userID, groupName) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
        con.query(`DELETE FROM ${messageDB}.${groupTable} WHERE id = ? AND groupName = ?`, [userID, groupName], function(err, result) {
            if (err) {
                console.log("Error removing user from group");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

/**
 * Retrieves all group entries from the database.
 * 
 * @returns {Promise<Array<Object>>} A promise that resolves to an array of group entries.
 */
async function getAllGroupEntries() {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve([]);
            return;
        }
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
            resolve(groupEntries);
        });
    });
}

/**
 * Creates a message table if it does not exist in the database.
 * 
 * @param {string} tableName - The name of the table to be created.
 * @returns {Promise<void>} - A promise that resolves when the table is created successfully.
 */
async function createMessageTableIfNotExist(tableName) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
        con.query(`CREATE TABLE IF NOT EXISTS ${messageDB}.${tableName} (id VARCHAR(64) PRIMARY KEY, 
            partnerName VARCHAR(255), incoming BOOLEAN, messageText TEXT, timestamp BIGINT, 
            timestampEdit BIGINT DEFAULT NULL, deleted BOOLEAN DEFAULT false, chatGroup VARCHAR(255) DEFAULT NULL)`, function(err, result) {
            if (err) {
                console.log("Error creating message table");
                reject(err);
                return;
            }
            resolve();
        });
    });
}

/**
 * Adds a message to the specified table in the database.
 * 
 * @param {string} tableName - The name of the table to add the message to.
 * @param {object} message - The message object containing the following properties:
 * @param {number} message.id - The ID of the message.
 * @param {string} message.partnerName - The name of the message partner.
 * @param {boolean} message.incoming - Indicates if the message is incoming or outgoing.
 * @param {string} message.messageText - The text of the message.
 * @param {string} message.timestamp - The timestamp of the message.
 * @returns {Promise<any>} A promise that resolves with the result of the insertion operation.
 */
async function addMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
        con.query(`INSERT INTO ${messageDB}.${tableName} (id, partnerName, incoming, messageText, timestamp) VALUES (?, ?, ?, ?, ?)`, 
            [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp], function(err, result) {
            if (err) {
                console.log("Error adding message");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

/**
 * Adds or edits a message in the specified table.
 * If the message already exists in the table, it updates the message.
 * If the message does not exist, it adds the message to the table.
 * 
 * @param {string} tableName - The name of the table.
 * @param {object} message - The message object to be added or edited.
 * @param {number} message.id - The ID of the message.
 * @param {string} message.partnerName - The name of the message partner.
 * @param {boolean} message.incoming - Indicates if the message is incoming or outgoing.
 * @param {string} message.messageText - The text of the message.
 * @param {string} message.timestamp - The timestamp of the message.
 * @param {string} message.timestampEdit - The timestamp of the message edit.
 * @returns {Promise<any>} - A promise that resolves with the result of the operation.
 */
async function addEditMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
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
                    resolve(result);
                });
            }
        });
    });
}


/**
 * Adds a deleted message to the specified table in the database.
 * If the message already exists in the table, it will be marked as deleted.
 * If the message does not exist, it will be added to the table with the deleted flag set to 1.
 *
 * @param {string} tableName - The name of the table in the database.
 * @param {object} message - The message object to be added or deleted.
 * @param {number} message.id - The ID of the message.
 * @param {string} message.partnerName - The name of the message partner.
 * @param {boolean} message.incoming - Indicates whether the message is incoming or outgoing.
 * @param {string} message.messageText - The text of the message.
 * @param {string} message.timestamp - The timestamp of the message.
 * @returns {Promise<any>} A promise that resolves with the result of the database operation.
 * @throws {Error} If there is an error checking if the message exists, deleting the message, or adding the message.
 */
async function addDeleteMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
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
                    resolve(result);
                });
            }
        });
    });
}

/**
 * Adds a chat group to a message in the specified table.
 * 
 * @param {string} tableName - The name of the table to add the chat group to.
 * @param {object} message - The message object containing the id and chatGroup properties.
 * @returns {Promise} A promise that resolves with the result of the update operation.
 */
async function addGroupToMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
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
                    resolve(result);
                });
            }
        });
    });
}

/**
 * Retrieves messages from the specified table in the database.
 * 
 * @param {string} tableName - The name of the table to retrieve messages from.
 * @returns {Promise<Array<Object>>} - A promise that resolves to an array of message objects.
 * Each message object contains the following properties:
 * - id: The ID of the message.
 * - partnerName: The name of the message partner.
 * - incoming: A boolean indicating whether the message is incoming or outgoing.
 * - messageText: The text of the message.
 * - timestamp: The timestamp of the message.
 * - timestampEdit: The timestamp of the last edit made to the message.
 * - deleted: A boolean indicating whether the message is deleted or not.
 * - chatGroup: The chat group associated with the message (optional).
 * @throws {Error} - If there is an error retrieving the messages.
 */
async function getMessages(tableName) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve([]);
            return;
        }
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
                resolve(messages);
            });
        } catch (error) {
            console.log("Error getting messages");
            reject();
        }
    });
}

/**
 * Clears the specified table in the database.
 * 
 * @param {string} tableName - The name of the table to be cleared.
 * @returns {Promise<any>} - A promise that resolves with the result of the table clearing operation.
 */
async function clearTable(tableName) {
    return new Promise((resolve, reject) => {
        if (!isInitialized) {
            resolve();
            return;
        }
        con.query(`DELETE FROM ${messageDB}.${tableName}`, function(err, result) {
            if (err) {
                console.log("Error clearing table");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

// Export the functions
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